package com.joinbe.data.collector;

import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.netty.handler.ServerHandler;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.data.collector.service.dto.*;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.dto.EquipmentDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
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
    private  EquipmentService equipmentService;

    /**
     *
     * @param deviceId
     * @return
     */
    /*@PostMapping("/location")
    @ApiOperation("获取设备的实时位置")
    public DeferredResult<Object> getLocation(@RequestBody @Valid LocationInfo locationInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/device validate error: {}", message);
            return new ResponseEntity<>(new LocationResponseDTO(1, message), HttpStatus.OK);
        }

        HashMap<String, String> params = new HashMap<>(8);
        Cmd cmd = factory.createInstance(EventEnum.GPOS.getEvent());
        if (cmd == null) {
            throw new BadRequestAlertException("Unimplemented command", "equipmentCmd", "invalidCmd");
        }
        DeferredResult<Object> deferredResult = new DeferredResult<>(3000L, "Get Location time out");
        String str = cmd.initCmd(params);
        logger.debug("REST request for get location, command: {}", str);
        serverHandler.sendMessage(deviceId, str, deferredResult);
        return deferredResult;
    }*/

    @PostMapping("/location")
    @ApiOperation("获取设备的实时位置")
    public DeferredResult<ResponseEntity<ResponseDTO>> getLocation(@RequestBody @Valid LocationInfo locationInfo, BindingResult bindingResult) {
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(3000L, "Get Location time out");

        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/device validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }

        HashMap<String, String> params = new HashMap<>(8);
        Cmd cmd = factory.createInstance(EventEnum.GPOS.getEvent());
        if (cmd == null) {
            throw new BadRequestAlertException("Unimplemented command", "equipmentCmd", "invalidCmd");
        }
        String str = cmd.initCmd(params);
        logger.debug("REST request for get location, command: {}", str);
        serverHandler.sendMessage(locationInfo.getDeviceId(), str, deferredResult);
        return deferredResult;
    }
    /**
     *
     * @param deviceInfo
     * @param bindingResult
     * @return
     */
    @PostMapping("/device")
    @ApiOperation("获取设备的信息")
    public ResponseEntity<ResponseDTO> getDeviceInfo(@RequestBody @Valid DeviceInfo deviceInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/device validate error: {}", message);
            return new ResponseEntity<>(new DeviceResponseDTO(1, message), HttpStatus.OK);
        }

        logger.debug("REST request to get Equipment : {}", deviceInfo);
        DeviceResponseDTO deviceResponseDTO = new DeviceResponseDTO(0, "success");
        Optional<EquipmentDTO> equipmentDto = equipmentService.findByLicensePlateNumber(deviceInfo.getPlateNumber());
        if(equipmentDto.isPresent()){
            deviceResponseDTO.setIdentifyNumber(equipmentDto.get().getIdentifyNumber());
            deviceResponseDTO.setImei(equipmentDto.get().getImei());
            deviceResponseDTO.setVersion(equipmentDto.get().getVersion());
        }else{
            deviceResponseDTO.setMessage("Not found the device by the plate number: " + deviceInfo.getPlateNumber() );
        }
        return new ResponseEntity<>(deviceResponseDTO, HttpStatus.OK);
    }
}
