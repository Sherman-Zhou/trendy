package com.joinbe.data.collector;

import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.cmd.register.impl.*;
import com.joinbe.data.collector.netty.handler.ServerHandler;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.data.collector.service.DataCollectService;
import com.joinbe.data.collector.service.dto.*;
import com.joinbe.data.collector.store.LocalEquipmentStroe;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.EquipmentOperationRecord;
import com.joinbe.domain.Vehicle;
import com.joinbe.domain.enumeration.EventCategory;
import com.joinbe.domain.enumeration.EventType;
import com.joinbe.domain.enumeration.OperationResult;
import com.joinbe.domain.enumeration.OperationSourceType;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.service.EquipmentService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;

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

    @Value("${netty.query-timeout}")
    private Long queryTimeout;

    @Autowired
    private DataCollectService dataCollectService;
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
     * @param bleVehicleReq
     * @param bindingResult
     * @return
     */
    @ApiOperation("根据车牌号设置蓝牙名称")
    @PostMapping("/setBluetooth")
    public DeferredResult<ResponseEntity<ResponseDTO>> setBluetooth(@RequestBody @Valid BleVehicleReq bleVehicleReq, BindingResult bindingResult) {
        logger.debug("REST request for set bluetooth name: {}", bleVehicleReq);
        ResponseEntity<BleResponseDTO> timeoutResponseDTOResponseEntity = new ResponseEntity<>(new BleResponseDTO(1, "set bluetooth name time out, maybe device is disconnecting, please try later, vehicle: " + bleVehicleReq.getPlateNumber()), HttpStatus.OK);
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, timeoutResponseDTOResponseEntity);
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/equipment/setBluetooth validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        Optional<Equipment> equipment = equipmentService.findByLicensePlateNumber(bleVehicleReq.getPlateNumber());
        String deviceId;
        if(equipment.isPresent()){
            deviceId = equipment.get().getImei();
        }else{
            String message = "No binding device found for the plate number : " + bleVehicleReq.getPlateNumber();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        //Do validation
        String bleName = equipment.get().getBluetoothName();
        if(bleVehicleReq.getBleName().equals(bleName)){
            String message = "No changes for current bluetooth name: " + bleVehicleReq.getBleName();
            logger.debug(message);
            deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }else{
            Optional<Equipment> equipmentByBluetoothName = equipmentService.findByBluetoothName(bleVehicleReq.getBleName());
            if(equipmentByBluetoothName.isPresent()){
                String message = "Bluetooth name already used by other equipment, imei is:" + equipmentByBluetoothName.get().getImei();
                logger.debug(message);
                deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, message), HttpStatus.OK));
                return deferredResult;
            }
        }

        HashMap<String, String> params = new HashMap<>(8);
        params.put(BleNameCmd.bleName, bleVehicleReq.getBleName());
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
}
