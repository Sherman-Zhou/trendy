package com.joinbe.data.collector.service;

import com.joinbe.config.Constants;
import com.joinbe.data.collector.netty.protocol.PositionProtocol;
import com.joinbe.data.collector.redistore.RedissonEquipmentStore;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.VehicleTrajectory;
import com.joinbe.domain.VehicleTrajectoryDetails;
import com.joinbe.domain.enumeration.VehicleStatusEnum;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.repository.VehicleTrajectoryRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;

@Service
public class DataCollectService {
    private final Logger log = LoggerFactory.getLogger(DataCollectService.class);

    @Autowired
    VehicleTrajectoryRepository vehicleTrajectoryRepository;
    @Autowired
    private RedissonEquipmentStore redissonEquipmentStore;
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void saveTrajectory(PositionProtocol msg) {
        if (msg == null || StringUtils.isBlank(msg.getUnitId())) {
            return;
        }
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(msg.getUnitId());
        if (!equipment.isPresent()) {
            log.warn("Equipment not maintained yet, imei: {}", msg.getUnitId());
            return;
        } else if (equipment.get().getVehicle() == null) {
            log.warn("vehicle not bound yet, imei: {}", msg.getUnitId());
            return;
        }
        //之前状态
        VehicleStatusEnum previousDeviceStatus = redissonEquipmentStore.getDeviceStatus(msg.getUnitId());
        //状态处理
        handleRealTimeSpeed(msg);
        //当前状态
        VehicleStatusEnum currentDeviceStatus = redissonEquipmentStore.getDeviceStatus(msg.getUnitId());

        log.debug("DeviceId: {}, previousDeviceStatus: {},  currentDeviceStatus: {}",msg.getUnitId(), previousDeviceStatus.getCode(), currentDeviceStatus.getCode());

        String existTrajectoryId = redissonEquipmentStore.getDeviceTrajectory(msg.getUnitId());
        VehicleTrajectory existTrajectory = null;
        VehicleTrajectory newVehicleTrajectory = null;
        if(StringUtils.isNotBlank(existTrajectoryId)){
            Optional<VehicleTrajectory> trajectory = vehicleTrajectoryRepository.findOneByTrajectoryId(existTrajectoryId);
            if(trajectory.isPresent()){
                existTrajectory = trajectory.get();
            }
        }
        //Generate trajectory detail
        VehicleTrajectoryDetails vehicleTrajectoryDetails = new VehicleTrajectoryDetails();
        vehicleTrajectoryDetails.setReceivedTime(Instant.now());
        vehicleTrajectoryDetails.setLongitude(BigDecimal.valueOf(msg.getLongitude()));
        vehicleTrajectoryDetails.setLatitude(BigDecimal.valueOf(msg.getLatitude()));
        vehicleTrajectoryDetails.setActualSpeed(BigDecimal.valueOf(msg.getSpeed()));
        vehicleTrajectoryDetails.setMileage(BigDecimal.valueOf(msg.getMileage()));
        /**
         * TODO - Generate trajectory by Policy
         * 规则：当静止状运行/未知状态/运行状态发生转换时，新产生轨迹
         */
        if(StringUtils.isBlank(existTrajectoryId) || existTrajectory == null ||
            previousDeviceStatus == null || !previousDeviceStatus.equals(currentDeviceStatus)){
            String newTrajectoryId = redissonEquipmentStore.genId();
            log.debug("Generate new trajectory: {}", newTrajectoryId);

            newVehicleTrajectory = new VehicleTrajectory();
            newVehicleTrajectory.setTrajectoryId(newTrajectoryId);
            newVehicleTrajectory.setStartLongitude(BigDecimal.valueOf(msg.getLongitude()));
            newVehicleTrajectory.setStartLatitude(BigDecimal.valueOf(msg.getLatitude()));
            newVehicleTrajectory.setStartTime(Instant.now());
            newVehicleTrajectory.setEquipment(equipment.get());
            newVehicleTrajectory.setVehicle(equipment.get().getVehicle());
            newVehicleTrajectory.setDetails(new HashSet<VehicleTrajectoryDetails>() {{ add(vehicleTrajectoryDetails);}});
            vehicleTrajectoryDetails.setVehicleTrajectory(newVehicleTrajectory);
            //设置之前的轨迹为结束
            if(existTrajectory != null){
                existTrajectory.setEndTime(Instant.now());
                //TODO - 计算之前轨迹的总里程数， 超速次数
            }
            //覆盖之前的轨迹
            redissonEquipmentStore.putInRedisForTrajectory(msg.getUnitId(), newTrajectoryId);
        }else{
            log.debug("Use exist trajectory, {}", existTrajectoryId);
            existTrajectory.getDetails().add(vehicleTrajectoryDetails);
            vehicleTrajectoryDetails.setVehicleTrajectory(existTrajectory);
        }
        //save
        if(existTrajectory != null){
            vehicleTrajectoryRepository.save(existTrajectory);
        }
        if(newVehicleTrajectory != null){
            vehicleTrajectoryRepository.save(newVehicleTrajectory);
        }
    }

    /**
     * 车辆实时状态监控
     * 车辆状态规则： 连续3分钟车辆为速度0，则为停止状态， 否则为运行
     * 车辆状态实现： 记录上次速度不为0的时间，当速度为0时： 当前时间 - 上次速度不为0的时间 > 3分钟则为静止
     */
    private void handleRealTimeSpeed(PositionProtocol msg) {
        if(msg.getSpeed() == null || StringUtils.isBlank(msg.getUnitId())){
            return;
        }
        int speed = msg.getSpeed().intValue();
        String unitId = msg.getUnitId();
        VehicleStatusEnum currentDeviceStatus = redissonEquipmentStore.getDeviceStatus(unitId);
        if(speed > 0 && !VehicleStatusEnum.RUNNING.equals(currentDeviceStatus)){
            //设置为运行中
            redissonEquipmentStore.putInRedisForStatus(unitId,VehicleStatusEnum.RUNNING);
        }else if(speed > 0){
            //记录不为0的时间
            redissonEquipmentStore.putInRedisForCalcStatus(unitId, System.currentTimeMillis());
        }

        //速度为0，当前时间 - 上次速度不为0的时间 > 3分钟则为静止
        if(speed == 0){
            //获取上次速度不为0的时间
            Long lastTimeStamp = redissonEquipmentStore.getDeviceCalcStatus(unitId);
            boolean  isStopped = (System.currentTimeMillis() - lastTimeStamp) / (1000 * 60) > Constants.VEHICLE_STOPPED_TIMELINE_MINUS;
            if(lastTimeStamp == null || isStopped || VehicleStatusEnum.UNKNOWN.equals(currentDeviceStatus)){
                redissonEquipmentStore.putInRedisForStatus(unitId, VehicleStatusEnum.STOPPED);
            }
        }
    }
}
