package com.joinbe.data.collector;

import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.cmd.register.impl.LockCmd;
import com.joinbe.data.collector.cmd.register.impl.SetBatteryCmd;
import com.joinbe.data.collector.cmd.register.impl.SetUserEventCmd;
import com.joinbe.data.collector.netty.handler.ServerHandler;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.data.collector.service.dto.*;
import com.joinbe.domain.Vehicle;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.dto.EquipmentDTO;
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
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, "Lock/Unlock time out");
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/equipment/lock validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new LockResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        //get device id
        Optional<Vehicle> vehicle = vehicleRepository.findById(lockDeviceReq.getVehicleId());
        String deviceId;
        if(vehicle.isPresent() && vehicle.get().getEquipment() !=null){
            deviceId = vehicle.get().getEquipment().getImei();
        }else{
            String message = "No binding device found for the vehicle ID: " + lockDeviceReq.getVehicleId();
            deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        HashMap<String, String> params = new HashMap<>(8);
        if ("open".equalsIgnoreCase(lockDeviceReq.getMode())) {
            params.put(LockCmd.KEY_OUTPUT_NUM, "2");
        } else if("close".equalsIgnoreCase(lockDeviceReq.getMode())){
            params.put(LockCmd.KEY_OUTPUT_NUM, "3");
        }else{
            deferredResult.setResult(new ResponseEntity<>(new LockResponseDTO(1, "Unknown lock mode."), HttpStatus.OK));
            return deferredResult;
        }
        params.put(LockCmd.KEY_OUTPUT_STATUS, "1");
        params.put(LockCmd.KEY_OUTPUT_ON_DURATION, "10");
        params.put(LockCmd.KEY_OUTPUT_OFF_DURATION, "10");
        params.put(LockCmd.KEY_OUTPUT_TIMES, "2");
        Cmd cmd = factory.createInstance(EventEnum.SGPO.getEvent());
        if (cmd == null) {
            deferredResult.setResult(new ResponseEntity<>(new LockResponseDTO(1, "Unimplemented command, please check with admin"), HttpStatus.OK));
            return deferredResult;
        }
        String sgpoStr = cmd.initCmd(params);
        logger.debug("REST request for lock/unlock, command: {}", sgpoStr);
        serverHandler.sendMessage(deviceId, sgpoStr, EventEnum.SGPO, deferredResult);
        return deferredResult;
    }
}
