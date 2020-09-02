package com.joinbe.data.collector;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.cmd.register.impl.*;
import com.joinbe.data.collector.netty.handler.ServerHandler;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.data.collector.service.DataCollectService;
import com.joinbe.data.collector.service.dto.*;
import com.joinbe.data.collector.store.LocalEquipmentStroe;
import com.joinbe.data.collector.store.RedissonEquipmentStore;
import com.joinbe.domain.*;
import com.joinbe.domain.enumeration.EventCategory;
import com.joinbe.domain.enumeration.EventType;
import com.joinbe.domain.enumeration.OperationResult;
import com.joinbe.domain.enumeration.OperationSourceType;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.repository.SystemConfigRepository;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.repository.VehicleTrajectoryDetailsRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.security.UserLoginInfo;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.util.RestfulClient;
import com.joinbe.web.rest.vm.VehicleMaintenanceVM;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.async.DeferredResult;

import javax.persistence.criteria.JoinType;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/internal/equipment")
public class EquipmentController {
    private final Logger logger = LoggerFactory.getLogger(EquipmentController.class);
    @Autowired
    ServerHandler serverHandler;

    @Autowired
    CmdRegisterFactory factory;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleTrajectoryDetailsRepository vehicleTrajectoryDetailsRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Autowired
    private RedissonEquipmentStore redissonEquipmentStore;

    @Value("${netty.query-timeout}")
    private Long queryTimeout;

    @Value("${netty.server-url}")
    private String serverUrl;

    @Autowired
    private DataCollectService dataCollectService;

    @Autowired
    private RestfulClient restfulClient;

    /**
     *
     * @param deviceId
     * @return
     */
    @GetMapping("/setUserEvent")
    public String setUserEvent(@RequestParam String deviceId) {
        HashMap<String, String> params = new HashMap<>(16);
        params.put(SetUserEventCmd.KEY_USER_ID, "100");
        params.put(SetUserEventCmd.KEY_ACTION, "3");
        params.put(SetUserEventCmd.KEY_SMS_VIP_MASK, "1");
        params.put(SetUserEventCmd.KEY_OUTPUT_CTRL_ID, "8");
        params.put(SetUserEventCmd.KEY_INPUT_MASK, "2");
        params.put(SetUserEventCmd.KEY_INPUT_CTRL, "2");
        params.put(SetUserEventCmd.KEY_SCHEDULE_ID, "0");
        params.put(SetUserEventCmd.KEY_ZONE_CTRL_ID, "0");
        params.put(SetUserEventCmd.KEY_EVENT_ID, "0");

        Cmd cmd = factory.createInstance(EventEnum.SEVT.getEvent());
        if (cmd == null) {
            return "";
        }
        String str = cmd.initCmd(params);
        logger.debug("REST request for setUserEvent, command: {}", str);
        serverHandler.sendMessage(deviceId, str);
        return str;
    }

    /**
     *
     * @param deviceId
     * @param mode
     * @return
     */
    @GetMapping("/setBatteryEvent")
    public String setBatteryEvent(@RequestParam String deviceId, @RequestParam String mode) {
        HashMap<String, String> params = new HashMap<>(8);
        params.put(SetBatteryCmd.KEY_MODE, mode);

        Cmd cmd = factory.createInstance(EventEnum.SBAT.getEvent());
        if (cmd == null) {
            return "";
        }
        String str = cmd.initCmd(params);
        logger.debug("REST request for setBatteryEvent, command: {}", str);
        serverHandler.sendMessage(deviceId, str);
        return str;
    }

    /**
     *
     * @param lockDeviceReq
     * @param bindingResult
     * @return
     */
    @ApiOperation("开锁/关锁")
    @PostMapping("/lock")
    public DeferredResult<ResponseEntity<ResponseDTO>> lock(@RequestBody @Valid LockDeviceReq lockDeviceReq, BindingResult bindingResult) {
        ResponseEntity<DoorResponseDTO> timeoutResponseDTOResponseEntity = new ResponseEntity<>(new DoorResponseDTO(1, "Lock/Unlock time out, maybe device is disconnecting, please try later, vehicleId: " + lockDeviceReq.getVehicleId()), HttpStatus.OK);
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, timeoutResponseDTOResponseEntity);
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/equipment/lock validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new DoorResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        //get device id
        Optional<Vehicle> vehicle = vehicleRepository.findById(lockDeviceReq.getVehicleId());
        String deviceId;
        if (vehicle.isPresent() && vehicle.get().getEquipment() != null) {
            deviceId = vehicle.get().getEquipment().getImei();
        } else {
            String message = "No binding device found for the vehicle ID: " + lockDeviceReq.getVehicleId();
            deferredResult.setResult(new ResponseEntity<>(new DoorResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        //Remote call
        String server = redissonEquipmentStore.getForServer(deviceId);
        logger.debug("Server info: deviceId: {}, server: {}", deviceId, server);
        /*if(StringUtils.isNotEmpty(server) && !server.equals(serverUrl)){
            StringBuffer callUrl = new StringBuffer().append(server).append("/lock");
            logger.debug("Server remote call for equipment Lock request, device: {}, callUrl:{} ", deviceId, callUrl);
            DoorResponseDTO doorResponse;
            try {
                doorResponse = restfulClient.postForObject(callUrl.toString(),lockDeviceReq, DoorResponseDTO.class);
                deferredResult.setResult(new ResponseEntity<>(doorResponse, HttpStatus.OK));
                return deferredResult;
            } catch (RestClientException e) {
                String message = "Server remote call failed for equipment Lock request, errorInfo: " + e.getMessage();
                logger.error(message);
                deferredResult.setResult(new ResponseEntity<>(new DoorResponseDTO(1, message), HttpStatus.OK));
                return deferredResult;
            }
        }*/

        EquipmentOperationRecord equipmentOperationRecord = new EquipmentOperationRecord();
        equipmentOperationRecord.setOperationSourceType(OperationSourceType.PLATFORM);
        equipmentOperationRecord.setEventType(EventCategory.LOCK);
        equipmentOperationRecord.setEquipment(vehicle.get().getEquipment());
        equipmentOperationRecord.setVehicle(vehicle.get());

        HashMap<String, String> params = new HashMap<>(8);
        if ("open".equalsIgnoreCase(lockDeviceReq.getMode())) {
            params.put(DoorCmd.MODE, "1");
            equipmentOperationRecord.setEventDesc(EventType.UNLOCK);
        } else if ("close".equalsIgnoreCase(lockDeviceReq.getMode())) {
            params.put(DoorCmd.MODE, "0");
            equipmentOperationRecord.setEventDesc(EventType.LOCK);
        } else {
            deferredResult.setResult(new ResponseEntity<>(new DoorResponseDTO(1, "Unknown lock mode."), HttpStatus.OK));
            return deferredResult;
        }
        Cmd cmd = factory.createInstance(EventEnum.DOOR.getEvent());
        if (cmd == null) {
            deferredResult.setResult(new ResponseEntity<>(new DoorResponseDTO(1, "Unimplemented command, please check with admin"), HttpStatus.OK));
            return deferredResult;
        }
        deferredResult.onTimeout(() -> {
            //remove from local store if timeout
            logger.warn("Lock/Unlock time out, maybe device is disconnecting, device: {}", deviceId);
            LocalEquipmentStroe.get(deviceId, EventEnum.DOOR);
            equipmentOperationRecord.setResult(OperationResult.FAILURE);
            dataCollectService.insertEventLog(equipmentOperationRecord);
        });
        deferredResult.setResultHandler((result) ->{
            if(result != null && result instanceof ResponseEntity){
                ResponseEntity responseEntity = (ResponseEntity) result;
                if(responseEntity !=null && responseEntity.getBody() !=null && responseEntity.getBody() instanceof ResponseDTO){
                    int code = ((ResponseDTO)responseEntity.getBody()).getCode();
                    String message = ((ResponseDTO)responseEntity.getBody()).getMessage();
                    if(code == 0){
                        equipmentOperationRecord.setResult(OperationResult.SUCCESS);
                    }else{
                        equipmentOperationRecord.setResult(OperationResult.FAILURE);
                    }
                    dataCollectService.insertEventLog(equipmentOperationRecord);
                }
            }
        });
        String doorStr = cmd.initCmd(params);
        logger.debug("REST request for lock/unlock, command: {}", doorStr);
        serverHandler.sendCommonQueryMessage(deviceId, doorStr, EventEnum.DOOR, deferredResult);
        return deferredResult;
    }

    /**
     *
     * @param bleDeviceReq
     * @param bindingResult
     * @return
     */
    @ApiOperation("根据设备的IMEI设置蓝牙名称")
    @PostMapping("/setBluetooth")
    public DeferredResult<ResponseEntity<ResponseDTO>> setBluetooth(@RequestBody @Valid BleDeviceReq bleDeviceReq, BindingResult bindingResult) {
        logger.debug("REST request for set bluetooth name: {}", bleDeviceReq);
        ResponseEntity<BleResponseDTO> timeoutResponseDTOResponseEntity = new ResponseEntity<>(new BleResponseDTO(1, "set bluetooth name time out, maybe device is disconnecting, please try later, device: " + bleDeviceReq.getImei()), HttpStatus.OK);
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, timeoutResponseDTOResponseEntity);
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/equipment/setBluetooth validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(bleDeviceReq.getImei());
        if (!equipment.isPresent()) {
            String message = "Equipment not maintained yet : " + bleDeviceReq.getImei();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        //Do validation
        String bleName = equipment.get().getBluetoothName();
        String deviceId = equipment.get().getImei();
        /*if(bleDeviceReq.getBleName().equals(bleName)){
            String message = "No changes for current bluetooth name: " + bleDeviceReq.getBleName();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }else{
            Optional<Equipment> equipmentByBluetoothName = equipmentService.findByBluetoothName(bleDeviceReq.getBleName());
            if(equipmentByBluetoothName.isPresent()){
                String message = "Bluetooth name already used by other equipment, imei is:" + equipmentByBluetoothName.get().getImei();
                logger.debug(message);
                deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, message), HttpStatus.OK));
                return deferredResult;
            }
        }*/
        Optional<Equipment> equipmentByBluetoothName = equipmentService.findByBluetoothName(bleDeviceReq.getBleName());
        if(equipmentByBluetoothName.isPresent() && !bleDeviceReq.getImei().equals(equipmentByBluetoothName.get().getImei())){
            String message = "Bluetooth name already used by other equipment, imei is:" + equipmentByBluetoothName.get().getImei();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        HashMap<String, String> params = new HashMap<>(8);
        params.put(BleNameCmd.bleName, bleDeviceReq.getBleName());
        Cmd cmd = factory.createInstance(EventEnum.BLENAME.getEvent());
        if (cmd == null) {
            deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, "Unimplemented command, please check with admin"), HttpStatus.OK));
            return deferredResult;
        }
        String bleNameStr = cmd.initCmd(params);
        logger.debug("REST request for set bluetooth name, command: {}", bleNameStr);
        serverHandler.sendCommonQueryMessage(deviceId, bleNameStr, EventEnum.BLENAME, deferredResult);
        deferredResult.onTimeout(() -> {
            //remove from local store if timeout
            logger.warn("Set bluetooth name time out, maybe device is disconnecting, device: {}", deviceId);
            LocalEquipmentStroe.get(deviceId, EventEnum.BLENAME);
        });
        return deferredResult;
    }

    /**
     *
     * @param vm
     * @param bindingResult
     * @return
     */
    @GetMapping("/vehicleMileageAndFuelCalc")
    @ApiOperation("获取车辆剩余油量和目前里程数")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO> getVehicleMileageAndFuelCalc(@Valid VehicleMaintenanceVM vm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/internal/equipment/vehicleMileageAndFuelCalc validate error: {}", message);
            return new ResponseEntity<>(new VehicleCalcInfoResponseDTO(1, message), HttpStatus.OK);
        }

        logger.debug("REST request to get VehicleMileageAndFuelCalc : {}", vm.toString());
        VehicleCalcInfoResponseDTO vehicleCalcInfoResponseDTO  = new VehicleCalcInfoResponseDTO(0, "success");
        try {
            //最新一次的车辆维护信息
            Optional<VehicleMaintenance> latestVehicleMaintenance = dataCollectService.findLatestVehicleMaintenance(Long.valueOf(vm.getVehicleId()));
            BigDecimal maintainedFuel = BigDecimal.ZERO;
            BigDecimal maintainedMileage = BigDecimal.ZERO;
            Instant latestMaintenanceTime = Instant.EPOCH;
            if(latestVehicleMaintenance.isPresent()){
                maintainedFuel = latestVehicleMaintenance.get().getFuel();
                maintainedMileage = latestVehicleMaintenance.get().getMileage();
                latestMaintenanceTime = latestVehicleMaintenance.get().getLastModifiedDate();
            }else{
                Optional<Vehicle> vehicle = vehicleRepository.findById(Long.valueOf(vm.getVehicleId()));
                if(vehicle.isPresent()){
                    maintainedFuel = vehicle.get().getTankVolume() != null ?vehicle.get().getTankVolume() : BigDecimal.ZERO;
                }else{
                    String message = "Vehicle not maintained yet, vehicleId:" + vm.getVehicleId();
                    logger.debug(message);
                    return new ResponseEntity<>(new VehicleCalcInfoResponseDTO(1, message), HttpStatus.OK);
                }
            }

            //获取车辆维护后的油耗和里程数
            QueryParams<VehicleTrajectoryDetails> queryParams = new QueryParams<>();
            queryParams.setDistinct(true);
            if (StringUtils.isNotBlank(vm.getVehicleId())) {
                queryParams.and(new Filter("vehicleTrajectory.vehicle.id", Filter.Operator.eq, vm.getVehicleId()));
            }
            if (latestMaintenanceTime != null) {
                queryParams.and(new Filter("receivedTime", Filter.Operator.between, Arrays.asList(latestMaintenanceTime, Instant.now().atZone(ZoneId.systemDefault()).toInstant())));
            }
            queryParams.addJoihFetch("vehicleTrajectory", JoinType.LEFT);
            Specification<VehicleTrajectoryDetails> specification = Specification.where(queryParams);
            List<VehicleTrajectoryDetails> trajectoryDetailList = vehicleTrajectoryDetailsRepository.findAll(specification);
            BigDecimal fuelConsumption = BigDecimal.ZERO;
            BigDecimal totalMileage = BigDecimal.ZERO;
            BigDecimal totalFuelConsumption = BigDecimal.ZERO;
            if(trajectoryDetailList != null && trajectoryDetailList.size() > 0){
                Optional<VehicleTrajectoryDetails> oneTrajectoryDetail = trajectoryDetailList.stream().findAny();
                if(oneTrajectoryDetail.get().getVehicleTrajectory() != null && oneTrajectoryDetail.get().getVehicleTrajectory().getEquipment() !=null){
                    fuelConsumption = oneTrajectoryDetail.get().getVehicleTrajectory().getVehicle().getFuelConsumption();
                }
                List<VehicleTrajectoryDetails> sortedList = trajectoryDetailList.stream().filter(detail -> detail.getReceivedTime() != null)
                    .sorted(Comparator.comparing(VehicleTrajectoryDetails::getReceivedTime)).collect(Collectors.toList());
                //calculate total mileage
                if(sortedList.size() > 1){
                    for (int i = 1; i < sortedList.size(); i++) {
                        BigDecimal lastOne = sortedList.get(i-1).getMileage() != null ? sortedList.get(i-1).getMileage() : BigDecimal.ZERO;
                        BigDecimal currentOne = sortedList.get(i).getMileage() != null ? sortedList.get(i).getMileage() : BigDecimal.ZERO;
                        BigDecimal subtract = currentOne.subtract(lastOne);
                        if(subtract.compareTo(BigDecimal.ZERO) == 1){
                            totalMileage = totalMileage.add(subtract);
                        }
                    }
                }
                //calculate fuelConsumption;
                if(fuelConsumption != null && fuelConsumption.compareTo(BigDecimal.ZERO) == 1){
                    totalFuelConsumption = totalMileage.divide(fuelConsumption,2, BigDecimal.ROUND_HALF_UP);
                }
            }

            //final calculate
            VehicleCalcInfoResponseItemDTO data = new VehicleCalcInfoResponseItemDTO();
            BigDecimal remainFuelConsumptionInLiter = maintainedFuel.subtract(totalFuelConsumption);
            if(remainFuelConsumptionInLiter.compareTo(BigDecimal.ZERO) == 1){
                data.setRemainFuelConsumptionInLiter(remainFuelConsumptionInLiter);
            }else{
                data.setRemainFuelConsumptionInLiter(BigDecimal.ZERO);
            }
            data.setCurrentMileageInKM(maintainedMileage.add(totalMileage));
            vehicleCalcInfoResponseDTO.setData(data);
            logger.debug("End process vehicle mileage and fuel calc: {}", vehicleCalcInfoResponseDTO.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new VehicleCalcInfoResponseDTO(1, e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity<>(vehicleCalcInfoResponseDTO, HttpStatus.OK);
    }

    /**
     *
     * @param bMacReq
     * @param bindingResult
     * @return
     */
    @ApiOperation("根据设备的IMEI查询MAC-step1")
    @PostMapping("/queryBMac0")
    public DeferredResult<ResponseEntity<ResponseDTO>> queryBMac0(@RequestBody @Valid BMacReq bMacReq, BindingResult bindingResult) {
        logger.debug("REST request for query mac: {}", bMacReq);
        ResponseEntity<BMacResponseDTO> timeoutResponseDTOResponseEntity = new ResponseEntity<>(new BMacResponseDTO(1, "Query mac0 time out, maybe device is disconnecting, please try later, device: " + bMacReq.getImei()), HttpStatus.OK);
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, timeoutResponseDTOResponseEntity);
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/equipment/queryBMac0 validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new BMacResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(bMacReq.getImei());
        if (!equipment.isPresent()) {
            String message = "Equipment not maintained yet : " + bMacReq.getImei();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new BMacResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        HashMap<String, String> params = new HashMap<>(8);
        Cmd cmd = factory.createInstance(EventEnum.BMAC0.getEvent());
        if (cmd == null) {
            deferredResult.setResult(new ResponseEntity<>(new BMacResponseDTO(1, "Unimplemented command, please check with admin"), HttpStatus.OK));
            return deferredResult;
        }
        String bMac0Str = cmd.initCmd(params);
        logger.debug("REST request for query MAC0, command: {}", bMac0Str);
        serverHandler.sendCommonQueryMessage(bMacReq.getImei(), bMac0Str, EventEnum.BMAC, deferredResult);
        deferredResult.onTimeout(() -> {
            //remove from local store if timeout
            logger.warn("Query BMac0 time out, maybe device is disconnecting, device: {}", bMacReq.getImei());
            LocalEquipmentStroe.get(bMacReq.getImei(), EventEnum.BMAC);
        });
        return deferredResult;
    }

    /**
     *
     * @param bMacReq
     * @param bindingResult
     * @return
     */
    @ApiOperation("根据设备的IMEI查询MAC")
    @PostMapping("/queryBMac")
    public DeferredResult<ResponseEntity<ResponseDTO>> queryBMac(@RequestBody @Valid BMacReq bMacReq, BindingResult bindingResult) {
        logger.debug("REST request for query mac: {}", bMacReq);
        ResponseEntity<BMacResponseDTO> timeoutResponseDTOResponseEntity = new ResponseEntity<>(new BMacResponseDTO(1, "Query mac time out, maybe device is disconnecting, please try later, device: " + bMacReq.getImei()), HttpStatus.OK);
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, timeoutResponseDTOResponseEntity);
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/equipment/queryBMac validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new BMacResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(bMacReq.getImei());
        if (!equipment.isPresent()) {
            String message = "Equipment not maintained yet : " + bMacReq.getImei();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new BMacResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        HashMap<String, String> params = new HashMap<>(8);
        Cmd cmd = factory.createInstance(EventEnum.BMAC.getEvent());
        if (cmd == null) {
            deferredResult.setResult(new ResponseEntity<>(new BMacResponseDTO(1, "Unimplemented command, please check with admin"), HttpStatus.OK));
            return deferredResult;
        }
        String bMacStr = cmd.initCmd(params);
        logger.debug("REST request for query MAC, command: {}", bMacStr);
        serverHandler.sendCommonQueryMessage(bMacReq.getImei(), bMacStr, EventEnum.BMAC, deferredResult);
        deferredResult.onTimeout(() -> {
            //remove from local store if timeout
            logger.warn("Query BMac time out, maybe device is disconnecting, device: {}", bMacReq.getImei());
            LocalEquipmentStroe.get(bMacReq.getImei(), EventEnum.BMAC);
        });
        return deferredResult;
    }


    /**
     *
     * @param deviceInitMileageReq
     * @param bindingResult
     * @return
     */
    @ApiOperation("根据设备的IMEI设置里程数偏移值")
    @PostMapping("/setMileageOffset")
    public DeferredResult<ResponseEntity<ResponseDTO>> setMileageOffset(@RequestBody @Valid DeviceInitMileageReq deviceInitMileageReq, BindingResult bindingResult) {
        logger.debug("REST request for set init mileage: {}", deviceInitMileageReq);
        ResponseEntity<MileageResponseDTO> timeoutResponseDTOResponseEntity = new ResponseEntity<>(new MileageResponseDTO(1, "set init mileage time out, maybe device is disconnecting, please try later, device: " + deviceInitMileageReq.getImei()), HttpStatus.OK);
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, timeoutResponseDTOResponseEntity);
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/equipment/setMileageOffset validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new MileageResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(deviceInitMileageReq.getImei());
        if (!equipment.isPresent()) {
            String message = "Equipment not maintained yet : " + deviceInitMileageReq.getImei();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new MileageResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        //Get global mileage multiple configuraion
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        SystemConfig config = systemConfigRepository.findByKeyAndMerchantId(SystemConfig.MILEAGE_MULTIPLE, loginInfo.getMerchantId());
        //Do validation
        BigDecimal initMileage = equipment.get().getInitMileage() != null ? equipment.get().getInitMileage() : BigDecimal.ZERO;
        BigDecimal multipleMileage = config != null ? new BigDecimal(config.getValue()) : BigDecimal.ONE;
        String deviceId = equipment.get().getImei();
        if(deviceInitMileageReq.getInitMileage().compareTo(initMileage) == 0){
            String message = "No changes for current mileage offset setup: " + deviceInitMileageReq.getInitMileage();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new MileageResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        HashMap<String, String> params = new HashMap<>(8);
        params.put(SetMileageCmd.MILEAGE_MODE, "1"); // Always set to open
        params.put(SetMileageCmd.MILEAGE_OFFSET, String.valueOf(deviceInitMileageReq.getInitMileage()));
        params.put(SetMileageCmd.MILEAGE_MULTIPLE, String.valueOf(multipleMileage));

        Cmd cmd = factory.createInstance(EventEnum.SMIL.getEvent());
        if (cmd == null) {
            deferredResult.setResult(new ResponseEntity<>(new MileageResponseDTO(1, "Unimplemented command, please check with admin"), HttpStatus.OK));
            return deferredResult;
        }
        String smilStr = cmd.initCmd(params);
        logger.debug("REST request for set init mileage, command: {}", smilStr);
        serverHandler.sendCommonQueryMessage(deviceId, smilStr, EventEnum.SMIL, deferredResult);
        deferredResult.onTimeout(() -> {
            //remove from local store if timeout
            logger.warn("Set mileage offset time out, maybe device is disconnecting, device: {}", deviceId);
            LocalEquipmentStroe.get(deviceId, EventEnum.SMIL);
        });
        return deferredResult;
    }

    /**
     *
     * @param dlfwReq
     * @param bindingResult
     * @return
     */
    @ApiOperation("DLFW Command")
    @PostMapping("/dlfw")
    public DeferredResult<ResponseEntity<ResponseDTO>> dlfwCmd(@RequestBody @Valid DLFWReq dlfwReq, BindingResult bindingResult) {
        logger.debug("REST request for DLFW command: {}", dlfwReq);
        ResponseEntity<DLFWResponseDTO> timeoutResponseDTOResponseEntity = new ResponseEntity<>(new DLFWResponseDTO(1, "DLFW command is time out, maybe device is disconnecting, please try later, device: " + dlfwReq.getImei()), HttpStatus.OK);
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, timeoutResponseDTOResponseEntity);
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/equipment/dlfwCmd validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new DLFWResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(dlfwReq.getImei());
        if (!equipment.isPresent()) {
            String message = "Equipment not maintained yet : " + dlfwReq.getImei();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new DLFWResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        HashMap<String, String> params = new HashMap<>(8);
        if(StringUtils.isNotBlank(dlfwReq.getCmd())){
            params.put(DLFWCmd.CMD, dlfwReq.getCmd());
        }else{
            params.put(DLFWCmd.CMD, "1,GPRS,internet,220.135.124.143,JC,jc654321,AT35.41U.STD.JC.V1.004.04.BIN,/,D9EE,1,300");
        }

        Cmd cmd = factory.createInstance(EventEnum.DLFW.getEvent());
        if (cmd == null) {
            deferredResult.setResult(new ResponseEntity<>(new MileageResponseDTO(1, "Unimplemented command, please check with admin"), HttpStatus.OK));
            return deferredResult;
        }
        String dlfwCmd = cmd.initCmd(params);
        logger.debug("REST request for DLFW, command: {}", dlfwCmd);
        serverHandler.sendCommonQueryMessage(dlfwReq.getImei(), dlfwCmd, EventEnum.DLFW, deferredResult);
        deferredResult.onTimeout(() -> {
            //remove from local store if timeout
            logger.warn("DLFW time out, maybe device is disconnecting, device: {}", dlfwReq.getImei());
            LocalEquipmentStroe.get(dlfwReq.getImei(), EventEnum.DLFW);
        });
        return deferredResult;
    }

    /**
     *
     * @param commonReq
     * @param bindingResult
     * @return
     */
    @ApiOperation("Common Command")
    @PostMapping("/cmd")
    public DeferredResult<ResponseEntity<ResponseDTO>> commonCmd(@RequestBody @Valid CommonReq commonReq, BindingResult bindingResult) {
        logger.debug("REST request for common command: {}", commonReq);
        ResponseEntity<CommonResponseDTO> timeoutResponseDTOResponseEntity = new ResponseEntity<>(new CommonResponseDTO(1, "Command is time out, maybe device is disconnecting, please try later, device: " + commonReq.getImei()), HttpStatus.OK);
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, timeoutResponseDTOResponseEntity);
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/equipment/cmd validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new CommonResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(commonReq.getImei());
        if (!equipment.isPresent()) {
            String message = "Equipment not maintained yet : " + commonReq.getImei();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new CommonResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        String cmd = commonReq.getCmd() + DataCollectService.CMD_END;
        logger.debug("REST request for common command: {}", cmd);
        serverHandler.sendCommonQueryMessage(commonReq.getImei(), cmd, EventEnum.C_CMD, deferredResult);
        deferredResult.onTimeout(() -> {
            //remove from local store if timeout
            logger.warn("C_CMD time out, maybe device is disconnecting, device: {}", commonReq.getImei());
            LocalEquipmentStroe.get(commonReq.getImei(), EventEnum.C_CMD);
        });
        return deferredResult;
    }
}
