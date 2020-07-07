package com.joinbe.data.collector;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.cmd.register.impl.SetTokenCmd;
import com.joinbe.data.collector.netty.handler.ServerHandler;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.data.collector.service.DataCollectService;
import com.joinbe.data.collector.service.dto.*;
import com.joinbe.data.collector.store.LocalEquipmentStroe;
import com.joinbe.data.collector.store.RedissonEquipmentStore;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.EquipmentOperationRecord;
import com.joinbe.domain.VehicleTrajectoryDetails;
import com.joinbe.domain.enumeration.*;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.repository.VehicleTrajectoryDetailsRepository;
import com.joinbe.service.EquipmentService;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/external/equipment")
@Api(value ="租车平台和管理平台的接口", tags={"设备控制相关接口"}, produces = "application/json" )
public class EquipmentExtController {
    private final Logger logger = LoggerFactory.getLogger(EquipmentExtController.class);
    @Autowired
    private ServerHandler serverHandler;

    @Autowired
    private CmdRegisterFactory factory;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private VehicleTrajectoryDetailsRepository vehicleTrajectoryDetailsRepository;

    @Autowired
    private  EquipmentRepository equipmentRepository;

    @Autowired
    private DataCollectService dataCollectService;

    @Autowired
    private RedissonEquipmentStore redissonEquipmentStore;

    @Value("${netty.query-timeout}")
    private Long queryTimeout;

    @PostMapping("/location/device")
    @ApiOperation("根据设备IMEI获取设备的实时位置")
    public DeferredResult<ResponseEntity<ResponseDTO>> getLocation(@RequestBody @Valid LocationDeviceReq locationReq, BindingResult bindingResult) {
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, "Get location timout, maybe device is disconnecting, please try later, device: " + locationReq.getImei());
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/location/device validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        HashMap<String, String> params = new HashMap<>(8);
        Cmd cmd = factory.createInstance(EventEnum.GPOS.getEvent());
        if (cmd == null) {
            throw new BadRequestAlertException("Unimplemented command", "equipmentCmd", "invalidCmd");
        }
        String gposCmd = cmd.initCmd(params);
        logger.debug("REST request for get location, command: {}", gposCmd);
        serverHandler.sendLocationMessage(locationReq.getImei(), gposCmd, deferredResult);
        deferredResult.onTimeout(() -> {
            //remove from local store if timeout
            logger.warn("getLocation timout, maybe device is disconnecting, device: {}", locationReq.getImei());
            LocalEquipmentStroe.get(locationReq.getImei(),EventEnum.GPOS);
        });
        return deferredResult;
    }

    /**
     *
     * @param locationReq
     * @param bindingResult
     * @return
     */
    @PostMapping("/location/vehicle")
    @ApiOperation("根据车牌号获取设备的实时位置")
    @Transactional(readOnly = true)
    public DeferredResult<ResponseEntity<ResponseDTO>> getLocation(@RequestBody @Valid LocationVehicleReq locationReq, BindingResult bindingResult) {
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, "Get location timout, maybe device is disconnecting, please try later, plateNumber :"+ locationReq.getPlateNumber());
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/location/vehicle validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        //get device id
        Optional<Equipment> equipment = equipmentService.findByLicensePlateNumber(locationReq.getPlateNumber());
        String deviceId;
        if(equipment.isPresent()){
            deviceId = equipment.get().getImei();
        }else{
            String message = "No binding device found for the plate number: " + locationReq.getPlateNumber();
            logger.warn("In /api/external/equipment/location/vehicle, warn: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        HashMap<String, String> params = new HashMap<>(8);
        Cmd cmd = factory.createInstance(EventEnum.GPOS.getEvent());
        if (cmd == null) {
            throw new BadRequestAlertException("Unimplemented command", "equipmentCmd", "invalidCmd");
        }
        String gposCmd = cmd.initCmd(params);
        logger.debug("REST request for get location, command: {}", gposCmd);
        serverHandler.sendLocationMessage(deviceId, gposCmd, deferredResult);
        deferredResult.onTimeout(() -> {
            //remove from local store if timeout
            logger.warn("getLocation timout, maybe device is disconnecting, device: {}", deviceId);
            LocalEquipmentStroe.get(deviceId,EventEnum.GPOS);
        });
        return deferredResult;
    }

    /**
     *
     * @param deviceInfo
     * @param bindingResult
     * @return
     */
    @PostMapping("/device")
    @ApiOperation("根据车牌号获取设备的信息")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO> getDeviceInfo(@RequestBody @Valid DeviceReq deviceInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/device validate error: {}", message);
            return new ResponseEntity<>(new DeviceResponseDTO(1, message), HttpStatus.OK);
        }

        logger.debug("REST request to get Equipment : {}", deviceInfo);
        DeviceResponseDTO deviceResponseDTO = new DeviceResponseDTO(0, "success");
        Optional<Equipment> equipment = equipmentService.findByLicensePlateNumber(deviceInfo.getPlateNumber());
        if(equipment.isPresent()){
            DeviceResponseItem deviceResponseItem = new DeviceResponseItem();
            deviceResponseItem.setIdentifyNumber(equipment.get().getIdentifyNumber());
            deviceResponseItem.setImei(equipment.get().getImei());
            deviceResponseItem.setVersion(equipment.get().getVersion());
            deviceResponseDTO.setData(deviceResponseItem);
        }else{
            deviceResponseDTO.setMessage("No binding device found for the plate number: " + deviceInfo.getPlateNumber() );
        }
        return new ResponseEntity<>(deviceResponseDTO, HttpStatus.OK);
    }


    @PostMapping("/token")
    @ApiOperation("生成设备的密钥")
    public DeferredResult<ResponseEntity<ResponseDTO>> genDeviceToken(@RequestBody @Valid DeviceReq deviceInfo, BindingResult bindingResult) {
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, "Gen device token time out, maybe device is disconnecting, please try later, Plate Number: " + deviceInfo.getPlateNumber());
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/token validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new TokenResponseDTO(1, message), HttpStatus.OK));
            return  deferredResult;
        }
        logger.debug("REST request to generate device's token : {}", deviceInfo);
        Optional<Equipment> equipment = equipmentService.findByLicensePlateNumber(deviceInfo.getPlateNumber());
        String deviceId;
        if(equipment.isPresent()){
            deviceId = equipment.get().getImei();
        }else{
            String message = "No binding device found for the plate number : " + deviceInfo.getPlateNumber();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new TokenResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        EquipmentOperationRecord equipmentOperationRecord = new EquipmentOperationRecord();
        equipmentOperationRecord.setOperationSourceType(OperationSourceType.APP);
        equipmentOperationRecord.setEventType(EventCategory.TOKEN);
        equipmentOperationRecord.setEquipment(equipment.get());
        equipmentOperationRecord.setVehicle(equipment.get().getVehicle());
        equipmentOperationRecord.setEventDesc(EventType.RELEASE);

        //生成md5 token: deviceId + expire time
        String expireDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().plusDays(7));
        StringBuffer md5OrigValue = new StringBuffer().append(deviceId).append(expireDateTime);
        String md5 = DigestUtils.md5DigestAsHex(md5OrigValue.toString().getBytes());

        HashMap<String, String> params = new HashMap<>(8);
        params.put(SetTokenCmd.TOKEN, md5);
        Cmd cmd = factory.createInstance(EventEnum.SETKEY.getEvent());
        if (cmd == null) {
            logger.error("Unimplemented command, please check with admin, plateNumber: {}", deviceInfo.getPlateNumber());
            deferredResult.setResult(new ResponseEntity<>(new TokenResponseDTO(1, "Unimplemented command, please check with admin"), HttpStatus.OK));
            return deferredResult;
        }
        String tokenStr = cmd.initCmd(params);
        logger.debug("REST request for write token, command: {}", tokenStr);
        serverHandler.sendCommonQueryMessage(deviceId, tokenStr, EventEnum.SETKEY, deferredResult);
        deferredResult.onTimeout(() -> {
            //remove from local store if timeout
            logger.warn("Gen device token time out, maybe device is disconnecting, device: {}", deviceId);
            LocalEquipmentStroe.get(deviceId,EventEnum.SETKEY);
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
        return deferredResult;
    }

    /**
     * @param trajectoryReq
     * @param bindingResult
     * @return
     */
    @PostMapping("/trajectory")
    @ApiOperation("根据时间段查询车辆的轨迹列表")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO> getTrajectory(@RequestBody @Valid TrajectoryReq trajectoryReq, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/trajectory validate error: {}", message);
            return new ResponseEntity<>(new DeviceResponseDTO(1, message), HttpStatus.OK);
        }

        logger.debug("REST request to get trajectory : {}", trajectoryReq.toString());
        TrajectoryResponseDTO trajectoryResponseDTO;
        try {
            Pageable pageable = PageRequest.of(trajectoryReq.getPageNum(), trajectoryReq.getPageSize());
            QueryParams<VehicleTrajectoryDetails> queryParams = new QueryParams<>();
            queryParams.setDistinct(true);
            if (StringUtils.isNotBlank(trajectoryReq.getPlateNumber())) {
                queryParams.and(new Filter("vehicleTrajectory.vehicle.licensePlateNumber", Filter.Operator.eq, trajectoryReq.getPlateNumber()));
            }
            if (trajectoryReq.getStartDateFrom() != null && trajectoryReq.getEndDateFrom() != null) {
                queryParams.and(new Filter("receivedTime", Filter.Operator.between, Arrays.asList(trajectoryReq.getStartDateFrom(), trajectoryReq.getEndDateFrom())));
            }

            Specification<VehicleTrajectoryDetails> specification = Specification.where(queryParams);
            Page<VehicleTrajectoryDetails> page = vehicleTrajectoryDetailsRepository.findAll(specification, pageable);

            // generate result orders
            trajectoryResponseDTO = new TrajectoryResponseDTO(0, "success", page.getTotalElements(), page.getNumber(), page.getSize());
            trajectoryResponseDTO.setData(page.map(vehicleTrajectoryDetails -> {
                TrajectoryResponseResult trajectoryDetails = new TrajectoryResponseResult();
                trajectoryDetails.setTrajectoryId(vehicleTrajectoryDetails.getVehicleTrajectory().getTrajectoryId());
                trajectoryDetails.setReceivedTime(vehicleTrajectoryDetails.getReceivedTime().getEpochSecond());
                trajectoryDetails.setLongitude(vehicleTrajectoryDetails.getLongitude());
                trajectoryDetails.setLatitude(vehicleTrajectoryDetails.getLatitude());
                trajectoryDetails.setActualSpeed(vehicleTrajectoryDetails.getActualSpeed());
                trajectoryDetails.setMileage(vehicleTrajectoryDetails.getMileage());
                trajectoryDetails.setLimitedSpeed(vehicleTrajectoryDetails.getLimitedSpeed());
                trajectoryDetails.setTirePressureFrontLeft(vehicleTrajectoryDetails.getTirePressureFrontLeft());
                trajectoryDetails.setTirePressureFrontRight(vehicleTrajectoryDetails.getTirePressureFrontRight());
                trajectoryDetails.setTirePressureRearLeft(vehicleTrajectoryDetails.getTirePressureRearLeft());
                trajectoryDetails.setTirePressureRearRight(vehicleTrajectoryDetails.getTirePressureRearRight());
                return trajectoryDetails;
            }).getContent());
            logger.debug("End process trajectory query: {}", trajectoryResponseDTO.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new TrajectoryResponseDTO(1, e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity<>(trajectoryResponseDTO, HttpStatus.OK);
    }

    /**
     *
     * @param uploadEventLogReq
     * @param bindingResult
     * @return
     */
    @PostMapping("/uploadEventLog")
    @ApiOperation("上传蓝牙开锁/关锁日志")
    @Transactional
    public ResponseEntity<ResponseDTO> uploadEventLog(@RequestBody @Valid UploadEventLogReq uploadEventLogReq, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/uploadEventLog validate error: {}", message);
            return new ResponseEntity<>(new ResponseDTO(1, message), HttpStatus.OK);
        }
        logger.debug("Start to process event log upload: {}", uploadEventLogReq.toString());

        if(EventType.resolve(uploadEventLogReq.getMode()) == null){
            return new ResponseEntity<>(new ResponseDTO(1, "Lock mode should be lock or unlock"), HttpStatus.OK);
        }

        if(OperationResult.resolve(uploadEventLogReq.getEventResult()) == null){
            return new ResponseEntity<>(new ResponseDTO(1, "Lock or unlock result should be success or failure"), HttpStatus.OK);
        }

        //get device info
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(uploadEventLogReq.getImei());
        if (!equipment.isPresent()) {
            return new ResponseEntity<>(new ResponseDTO(1, "Equipment not maintained yet, imei:" + uploadEventLogReq.getImei()), HttpStatus.OK);
        } else if (equipment.get().getVehicle() == null) {
            return new ResponseEntity<>(new ResponseDTO(1, "Vehicle not bound yet, imei:" + uploadEventLogReq.getImei()), HttpStatus.OK);
        }
        ResponseDTO responseDTO = new ResponseDTO(0, "Event log is successfully uploaded!");
        try {
            //Insert event log
            EquipmentOperationRecord equipmentOperationRecord = new EquipmentOperationRecord();
            equipmentOperationRecord.setOperationSourceType(OperationSourceType.APP);
            equipmentOperationRecord.setEventType(EventCategory.LOCK);
            equipmentOperationRecord.setEventDesc(EventType .resolve(uploadEventLogReq.getMode()));
            equipmentOperationRecord.setResult(OperationResult.resolve(uploadEventLogReq.getEventResult()));
            equipmentOperationRecord.setEquipment(equipment.get());
            equipmentOperationRecord.setVehicle(equipment.get().getVehicle());
            dataCollectService.insertEventLog(equipmentOperationRecord);
            logger.debug("insert app event log success, imie: {}", uploadEventLogReq.getImei());
        } catch (Exception e) {
            logger.error(e.getMessage());
            responseDTO = new ResponseDTO(1, e.getMessage());
        }

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    /**
     *
     * @param ibuttonReq
     * @param bindingResult
     * @return
     */
    @PostMapping("/iButtonStatus")
    @ApiOperation("根据设备的imie查询ibutton的状态")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO> getIButtonStatus(@RequestBody @Valid IButtonReq ibuttonReq, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/iButtonStatus validate error: {}", message);
            return new ResponseEntity<>(new IButtonResponseDTO(1, message), HttpStatus.OK);
        }

        logger.debug("REST request to get iButton status : {}", ibuttonReq.toString());
        IButtonResponseDTO iButtonResponseDTO = new IButtonResponseDTO(0, "success");
        Optional<Equipment> equipment = equipmentService.findByLicensePlateNumber(ibuttonReq.getPlateNumber());
        if(equipment.isPresent()){
            IButtonResponseItem iButtonResponseItem = new IButtonResponseItem();
            iButtonResponseItem.setImei(equipment.get().getImei());
            IbuttonStatusEnum deviceIButtonStatus = redissonEquipmentStore.getDeviceIButtonStatus(equipment.get().getImei());
            iButtonResponseItem.setiButtonStatus(deviceIButtonStatus !=null ? deviceIButtonStatus.getCode() : IbuttonStatusEnum.UNKNOWN.getCode());
            iButtonResponseItem.setiButtonId(redissonEquipmentStore.getDeviceIButtonId(equipment.get().getImei()));
            iButtonResponseDTO.setData(iButtonResponseItem);
        }else{
            iButtonResponseDTO.setMessage("No binding device found for the plate number: " + ibuttonReq.getPlateNumber() );
        }
        return new ResponseEntity<>(iButtonResponseDTO, HttpStatus.OK);
    }
}
