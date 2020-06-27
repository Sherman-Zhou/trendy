package com.joinbe.data.collector;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.cmd.register.impl.SetTokenCmd;
import com.joinbe.data.collector.netty.handler.ServerHandler;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.data.collector.service.dto.*;
import com.joinbe.domain.VehicleTrajectoryDetails;
import com.joinbe.repository.VehicleTrajectoryDetailsRepository;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.dto.EquipmentDTO;
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
    VehicleTrajectoryDetailsRepository vehicleTrajectoryDetailsRepository;

    @Value("${netty.query-timeout}")
    private Long queryTimeout;

    @PostMapping("/location/device")
    @ApiOperation("根据设备IMEI获取设备的实时位置")
    public DeferredResult<ResponseEntity<ResponseDTO>> getLocation(@RequestBody @Valid LocationDeviceReq locationReq, BindingResult bindingResult) {
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, "Get Location time out");

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
        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = new DeferredResult<>(queryTimeout, "Get Location time out");

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
            deviceId = equipmentDto.get().getImei();
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
        Optional<EquipmentDTO> equipmentDto = equipmentService.findByLicensePlateNumber(deviceInfo.getPlateNumber());
        if(equipmentDto.isPresent()){
            DeviceResponseItem deviceResponseItem = new DeviceResponseItem();
            deviceResponseItem.setIdentifyNumber(equipmentDto.get().getIdentifyNumber());
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
            deviceId = equipmentDto.get().getImei();
        }else{
            logger.debug("No binding device found for the plate number : {}", deviceInfo.getPlateNumber());
            tokenResponseDTO.setMessage("No binding device found for the plate number: " + deviceInfo.getPlateNumber());
            return new ResponseEntity<>(tokenResponseDTO, HttpStatus.OK);
        }

        //生成md5 token: deviceId + expire time
        String expireDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().plusDays(7));
        StringBuffer md5OrigValue = new StringBuffer().append(deviceId).append(expireDateTime);
        String md5 = DigestUtils.md5DigestAsHex(md5OrigValue.toString().getBytes());

        TokenResponseItem tokenResponseItem = new TokenResponseItem();
        tokenResponseItem.setImei(deviceId);
        tokenResponseItem.setToken(md5);
        tokenResponseItem.setExpireDate(expireDateTime);
        tokenResponseDTO.setData(tokenResponseItem);
        //TODO: write token to equipment
        HashMap<String, String> params = new HashMap<>(8);
        params.put(SetTokenCmd.TOKEN, md5);
        Cmd cmd = factory.createInstance(EventEnum.SETKEY.getEvent());
        if (cmd == null) {
            logger.error("Unimplemented command, please check with admin, plateNumber: {}", deviceInfo.getPlateNumber());
            //deferredResult.setResult(new ResponseEntity<>(new LockResponseDTO(1, "Unimplemented command, please check with admin"), HttpStatus.OK));
            //return deferredResult;
        }
        String tokenStr = cmd.initCmd(params);
        logger.debug("REST request for write token, command: {}", tokenStr);
        //serverHandler.sendMessage(deviceId, tokenStr, EventEnum.SETKEY, deferredResult);
        return new ResponseEntity<>(tokenResponseDTO, HttpStatus.OK);
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
}
