package com.joinbe.data.collector;

import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.netty.handler.ServerHandler;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.data.collector.service.dto.*;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.dto.EquipmentDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/location/device")
    @ApiOperation("根据设备id获取设备的实时位置")
    public DeferredResult<ResponseEntity<ResponseDTO>> getLocation(@RequestBody @Valid LocationDeviceReq locationReq, BindingResult bindingResult) {
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(3000L, "Get Location time out");

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
        serverHandler.sendMessage(locationReq.getDeviceId(), gposCmd, deferredResult);
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
    public DeferredResult<ResponseEntity<ResponseDTO>> getLocation(@RequestBody @Valid LocationVehicleReq locationReq, BindingResult bindingResult) {
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(3000L, "Get Location time out");

        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/location/vehicle validate error: {}", message);
            deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(1, message), HttpStatus.OK));
            return deferredResult;
        }
        //get device id
        Optional<EquipmentDTO> equipmentDto = equipmentService.findByLicensePlateNumber(locationReq.getPlateNumber());
        String deviceId;
        if(equipmentDto.isPresent()){
            deviceId = equipmentDto.get().getIdentifyNumber();
        }else{
            String message = "No binding device found for the plate number: " + locationReq.getPlateNumber();
            logger.warn("In /api/external/equipment/location/vehicle validate error: {}", message);
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
        serverHandler.sendMessage(deviceId, gposCmd, deferredResult);
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
    public ResponseEntity<ResponseDTO> getDeviceInfo(@RequestBody @Valid DeviceReq deviceInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/device validate error: {}", message);
            return new ResponseEntity<>(new DeviceResponseDTO(1, message), HttpStatus.OK);
        }

        logger.debug("REST request to get Equipment : {}", deviceInfo);
        DeviceResponseDTO deviceResponseDTO = new DeviceResponseDTO(0, "success");
        Optional<EquipmentDTO> equipmentDto = equipmentService.findByLicensePlateNumber(deviceInfo.getPlateNumber());
        if(equipmentDto.isPresent()){
            DeviceResponseItem deviceResponseItem = new DeviceResponseItem();
            deviceResponseItem.setDeviceId(equipmentDto.get().getIdentifyNumber());
            deviceResponseItem.setImei(equipmentDto.get().getImei());
            deviceResponseItem.setVersion(equipmentDto.get().getVersion());
            deviceResponseDTO.setData(deviceResponseItem);
        }else{
            deviceResponseDTO.setMessage("No binding device found for the plate number: " + deviceInfo.getPlateNumber() );
        }
        return new ResponseEntity<>(deviceResponseDTO, HttpStatus.OK);
    }


    @PostMapping("/token")
    @ApiOperation("生成设备的密钥")
    public ResponseEntity<ResponseDTO> genDeviceToken(@RequestBody @Valid DeviceReq deviceInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.warn("In /api/external/equipment/token validate error: {}", message);
            return new ResponseEntity<>(new DeviceResponseDTO(1, message), HttpStatus.OK);
        }

        logger.debug("REST request to generate device's token : {}", deviceInfo);
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(0, "success");
        Optional<EquipmentDTO> equipmentDto = equipmentService.findByLicensePlateNumber(deviceInfo.getPlateNumber());
        String deviceId;
        if(equipmentDto.isPresent()){
            deviceId = equipmentDto.get().getIdentifyNumber();
        }else{
            tokenResponseDTO.setMessage("No binding device found for the plate number: " + deviceInfo.getPlateNumber());
            return new ResponseEntity<>(tokenResponseDTO, HttpStatus.OK);
        }

        //生成md5 token: deviceId + expire time
        String expireDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().plusDays(7));
        StringBuffer md5OrigValue = new StringBuffer().append(deviceId).append(expireDateTime);
        String md5 = DigestUtils.md5DigestAsHex(md5OrigValue.toString().getBytes());

        TokenResponseItem tokenResponseItem = new TokenResponseItem();
        tokenResponseItem.setDeviceId(deviceId);
        tokenResponseItem.setToken(md5);
        tokenResponseItem.setExpireDate(expireDateTime);
        tokenResponseDTO.setData(tokenResponseItem);

        //TODO: write token to equipment
        return new ResponseEntity<>(tokenResponseDTO, HttpStatus.OK);
    }
}
