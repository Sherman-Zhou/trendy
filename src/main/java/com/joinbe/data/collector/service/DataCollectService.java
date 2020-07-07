package com.joinbe.data.collector.service;

import com.joinbe.config.Constants;
import com.joinbe.data.collector.netty.protocol.code.EventIDEnum;
import com.joinbe.data.collector.netty.protocol.message.PositionProtocol;
import com.joinbe.data.collector.store.RedissonEquipmentStore;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.EquipmentOperationRecord;
import com.joinbe.domain.VehicleTrajectory;
import com.joinbe.domain.VehicleTrajectoryDetails;
import com.joinbe.domain.enumeration.IbuttonStatusEnum;
import com.joinbe.domain.enumeration.VehicleStatusEnum;
import com.joinbe.repository.EquipmentOperationRecordRepository;
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
import java.util.Set;

@Service
public class DataCollectService {
    private final Logger log = LoggerFactory.getLogger(DataCollectService.class);

    @Autowired
    VehicleTrajectoryRepository vehicleTrajectoryRepository;
    @Autowired
    private RedissonEquipmentStore redissonEquipmentStore;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private  EquipmentOperationRecordRepository equipmentOperationRecordRepository;

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
        //处理event
        handleEvent(msg);
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
         *
         * 规则：当静止状运行/未知状态/运行状态发生转换时，新产生轨迹
         */
        if(StringUtils.isBlank(existTrajectoryId) || existTrajectory == null ||
            previousDeviceStatus == null || !previousDeviceStatus.equals(currentDeviceStatus)){
            String newTrajectoryId = redissonEquipmentStore.genId();
            log.info("Generate new trajectory: {}", newTrajectoryId);

            newVehicleTrajectory = new VehicleTrajectory();
            newVehicleTrajectory.setTrajectoryId(newTrajectoryId);
            newVehicleTrajectory.setStartLongitude(BigDecimal.valueOf(msg.getLongitude()));
            newVehicleTrajectory.setStartLatitude(BigDecimal.valueOf(msg.getLatitude()));
            newVehicleTrajectory.setStartTime(Instant.now());
            newVehicleTrajectory.setEquipment(equipment.get());
            newVehicleTrajectory.setVehicle(equipment.get().getVehicle());
            //第一条数据，不用计算, 为0
            newVehicleTrajectory.setMileage(BigDecimal.ZERO);
            newVehicleTrajectory.setDetails(new HashSet<VehicleTrajectoryDetails>() {{ add(vehicleTrajectoryDetails);}});
            vehicleTrajectoryDetails.setVehicleTrajectory(newVehicleTrajectory);
            //设置之前的轨迹为结束
            if(existTrajectory != null){
                existTrajectory.setEndTime(Instant.now());
                //TODO - 计算超速次数
            }
            //覆盖之前的轨迹
            redissonEquipmentStore.putInRedisForTrajectory(msg.getUnitId(), newTrajectoryId);
        }else{
            log.info("Use exist trajectory, {}", existTrajectoryId);
            existTrajectory.getDetails().add(vehicleTrajectoryDetails);
            //计算当前轨迹的实时里程
            BigDecimal mileage = handleTrajectoryMileage(existTrajectory);
            existTrajectory.setMileage(mileage);
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
     * 轨迹为累加值
     * @param existTrajectory
     */
    private BigDecimal handleTrajectoryMileage(VehicleTrajectory existTrajectory) {
        Set<VehicleTrajectoryDetails> trajectoryDetails = existTrajectory.getDetails();
        VehicleTrajectoryDetails maxTrajectoryDetails = trajectoryDetails.stream().filter(detail -> detail.getReceivedTime() != null)
            .max((trajectoryDetails1, trajectoryDetails2) -> {
                return trajectoryDetails1.getReceivedTime().compareTo(trajectoryDetails2.getReceivedTime());
            })
            .orElseGet(null);
        VehicleTrajectoryDetails minTrajectoryDetails = trajectoryDetails.stream().filter(detail -> detail.getReceivedTime() != null)
            .min((trajectoryDetails1, trajectoryDetails2) -> {
                return trajectoryDetails1.getReceivedTime().compareTo(trajectoryDetails2.getReceivedTime());
            })
            .orElseGet(null);
        if(maxTrajectoryDetails != null && minTrajectoryDetails != null && maxTrajectoryDetails.getMileage()!=null && minTrajectoryDetails.getMileage()!=null){
            BigDecimal maxMileage = maxTrajectoryDetails.getMileage();
            BigDecimal minMileage = minTrajectoryDetails.getMileage();
            log.debug("HandleTrajectoryMileage: trajectoryId: {}, minMileageDetailsId: {}, maxMileageDetailsId: {}", existTrajectory.getId(), minTrajectoryDetails.getId(),maxTrajectoryDetails.getId());
            if(maxMileage.compareTo(minMileage) == 0){
                return BigDecimal.ZERO;
            }else if(maxMileage.compareTo(minMileage) > 0){
                return maxMileage.subtract(minMileage);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 处理Event
     * @param msg
     */
    private void handleEvent(PositionProtocol msg) {
        Integer eventId = msg.getEventId();
        if(EventIDEnum.IBUTTON_ATTACHED.getEventId().equals(eventId)){
            log.info("IButton is attached, device:{}, iButtonId:{}", msg.getUnitId(), msg.getIbuttonId());
            redissonEquipmentStore.putInRedisForIButtonStatus(msg.getUnitId(), IbuttonStatusEnum.ATTACHED,msg.getIbuttonId());
            //for debug
            log.debug("IButton current status in redis, iButtonStatus:{}", redissonEquipmentStore.getDeviceIButtonStatus(msg.getUnitId()));
            log.debug("IButton current iButtonId in redis,iButtonId:{}", redissonEquipmentStore.getDeviceIButtonId(msg.getUnitId()));
        }else if(EventIDEnum.IBUTTON_REMOVED.getEventId().equals(eventId)){
            log.info("IButton is removed, device:{}, iButtonId:{}", msg.getUnitId(), msg.getIbuttonId());
            redissonEquipmentStore.putInRedisForIButtonStatus(msg.getUnitId(),IbuttonStatusEnum.REMOVED,msg.getIbuttonId());
            //for debug
            log.debug("IButton current status in redis, iButtonStatus:{}", redissonEquipmentStore.getDeviceIButtonStatus(msg.getUnitId()));
            log.debug("IButton current iButtonId in redis,iButtonId:{}", redissonEquipmentStore.getDeviceIButtonId(msg.getUnitId()));
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
            //设置为运行中 & 记录不为0的时间
            redissonEquipmentStore.putInRedisForStatus(unitId,VehicleStatusEnum.RUNNING);
            redissonEquipmentStore.putInRedisForCalcStatus(unitId, System.currentTimeMillis());
        }else if(speed > 0){
            //记录不为0的时间
            redissonEquipmentStore.putInRedisForCalcStatus(unitId, System.currentTimeMillis());
        }

        //速度为0，当前时间 - 上次速度不为0的时间 > 3分钟则为静止
        if(speed == 0){
            //获取上次速度不为0的时间
            Long lastTimeStamp = redissonEquipmentStore.getDeviceCalcStatus(unitId);
            //boolean  isStopped = (System.currentTimeMillis() - lastTimeStamp) / (1000 * 60) > Constants.VEHICLE_STOPPED_TIMELINE_MINUS;
            if(lastTimeStamp == null || (System.currentTimeMillis() - lastTimeStamp) / (1000 * 60) > Constants.VEHICLE_STOPPED_TIMELINE_MINUS || VehicleStatusEnum.UNKNOWN.equals(currentDeviceStatus)){
                redissonEquipmentStore.putInRedisForStatus(unitId, VehicleStatusEnum.STOPPED);
            }
        }
    }

    /**
     *
     * @param equipmentOperationRecord
     */
    @Transactional
    public void insertEventLog(EquipmentOperationRecord equipmentOperationRecord){
        if(equipmentOperationRecord == null){
            return;
        }
        equipmentOperationRecordRepository.save(equipmentOperationRecord);
    }
}
