package com.joinbe.data.collector.service;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.config.Constants;
import com.joinbe.data.collector.netty.protocol.code.EventIDEnum;
import com.joinbe.data.collector.netty.protocol.message.PositionProtocol;
import com.joinbe.data.collector.service.dto.ResponseDTO;
import com.joinbe.data.collector.service.dto.VehicleCalcInfoResponseDTO;
import com.joinbe.data.collector.service.dto.VehicleCalcInfoResponseItemDTO;
import com.joinbe.data.collector.store.RedissonEquipmentStore;
import com.joinbe.domain.*;
import com.joinbe.domain.enumeration.*;
import com.joinbe.repository.*;
import com.joinbe.web.rest.vm.VehicleMaintenanceVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.persistence.criteria.JoinType;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private EquipmentFaultRepository equipmentFaultRepository;
    @Autowired
    private VehicleMaintenanceRepository vehicleMaintenanceRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleTrajectoryDetailsRepository vehicleTrajectoryDetailsRepository;

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
        handleEvent(msg, equipment.get());
        //之前状态
        VehicleStatusEnum previousDeviceStatus = redissonEquipmentStore.getDeviceStatus(msg.getUnitId());
        VehicleFireStatusEnum previousFireStatus = redissonEquipmentStore.getDeviceFireStatus(msg.getUnitId());
        //处理Input状态
        handleInputStatus(msg);
        //处理车辆状态
        //handleRealTimeSpeed(msg);
        //当前状态
        VehicleStatusEnum currentDeviceStatus = redissonEquipmentStore.getDeviceStatus(msg.getUnitId());
        VehicleFireStatusEnum currentFireStatus = redissonEquipmentStore.getDeviceFireStatus(msg.getUnitId());

        log.debug("DeviceId: {}, previousDeviceStatus: {},  currentDeviceStatus: {}",msg.getUnitId(), previousDeviceStatus.getCode(), currentDeviceStatus.getCode());
        log.debug("DeviceId: {}, previousFireStatus: {},  currentFireStatus: {}",msg.getUnitId(), previousFireStatus.getCode(), currentFireStatus.getCode());

        //更新车辆状态
        if(previousDeviceStatus == null || VehicleStatusEnum.UNKNOWN.equals(previousDeviceStatus) || !previousDeviceStatus.equals(currentDeviceStatus)){
            boolean isRunning = VehicleStatusEnum.RUNNING.equals(currentDeviceStatus);
            log.debug("Vehicle's status is changed, update status, deviceNo: {}, isRunning: {}", msg.getUnitId(), isRunning);
            this.updateStatus(msg.getUnitId(),true,isRunning);
        }

        /**
         * 判断数据是否接收
         * 当前状态为关火 && 上一次状态为非开火
         */
        if(VehicleFireStatusEnum.CLOSE_FIRE.equals(currentFireStatus) && !VehicleFireStatusEnum.OPEN_FIRE.equals(previousFireStatus)){
            log.debug("Ignore the position data, imei: {}",msg.getUnitId());
            return;
        }

        /**
         * 数据保存
         */
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
         * 产生新轨迹： 当前为开火 && 上次为非开火
         * 更新轨迹： 当前为开火 && 上次也为开火
         * 结束轨迹： 当前为关火 && 上次为开火
         */
        if(VehicleFireStatusEnum.OPEN_FIRE.equals(currentFireStatus)){
            if(!VehicleFireStatusEnum.OPEN_FIRE.equals(previousFireStatus) || existTrajectory == null){
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
                //覆盖之前的轨迹
                redissonEquipmentStore.putInRedisForTrajectory(msg.getUnitId(), newTrajectoryId);
            }else if(existTrajectory != null){
                log.info("Use exist trajectory, {}", existTrajectoryId);
                existTrajectory.getDetails().add(vehicleTrajectoryDetails);
                //计算当前轨迹的实时里程
                BigDecimal mileage = handleTrajectoryMileage(existTrajectory);
                existTrajectory.setMileage(mileage);
                vehicleTrajectoryDetails.setVehicleTrajectory(existTrajectory);
            }
        }else if(existTrajectory != null){
                existTrajectory.setEndTime(Instant.now());
                //TODO - 计算超速次数
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
     * handle input status
     * @param msg
     */
    private void handleInputStatus(PositionProtocol msg) {
        Integer inputStatus = msg.getInputStatus();
        if(msg == null ||inputStatus == null || StringUtils.isBlank(msg.getUnitId())){
            return;
        }
        /**
         * 开锁：2,3,7
         * 关锁：0,1,4,5,6
         */
        if(inputStatus == 2 || inputStatus == 3 || inputStatus == 7){
            redissonEquipmentStore.putInRedisForDoorStatus(msg.getUnitId(), VehicleDoorStatusEnum.OPEN);
        }else{
            redissonEquipmentStore.putInRedisForDoorStatus(msg.getUnitId(), VehicleDoorStatusEnum.CLOSE);
        }
        /**
         * 点火：1,3,5,7
         * 熄火：0,2,4,6
         */
        if(inputStatus == 1 || inputStatus == 3 || inputStatus == 5 || inputStatus == 7){
            redissonEquipmentStore.putInRedisForFireStatus(msg.getUnitId(), VehicleFireStatusEnum.OPEN_FIRE);
            //规则: 开火状态 = 行驶状态
            redissonEquipmentStore.putInRedisForStatus(msg.getUnitId(),VehicleStatusEnum.RUNNING);
        }else{
            redissonEquipmentStore.putInRedisForFireStatus(msg.getUnitId(), VehicleFireStatusEnum.CLOSE_FIRE);
            //规则: 熄火状态 = 静止状态
            redissonEquipmentStore.putInRedisForStatus(msg.getUnitId(), VehicleStatusEnum.STOPPED);
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
    @Transactional(rollbackFor = Exception.class)
    public void handleEvent(PositionProtocol msg, Equipment equipment) {
        Integer eventId = msg.getEventId();
        if(equipment == null || eventId == null){
            return;
        }
        //generate event log
        EquipmentFault equipmentFault = new EquipmentFault();
        equipmentFault.setIsRead(false);
        equipmentFault.setVehicle(equipment.getVehicle());
        equipmentFault.setEquipment(equipment);

        if(EventIDEnum.IBUTTON_ATTACHED.getEventId().equals(eventId)){
            log.info("IButton is attached, device:{}, iButtonId:{}", msg.getUnitId(), msg.getIbuttonId());
            redissonEquipmentStore.putInRedisForIButtonStatus(msg.getUnitId(), IbuttonStatusEnum.ATTACHED);
            redissonEquipmentStore.putInRedisForIButtonId(msg.getUnitId(), msg.getIbuttonId());
        }else if(EventIDEnum.IBUTTON_REMOVED.getEventId().equals(eventId)){
            log.info("IButton is removed, device:{}, iButtonId:{}", msg.getUnitId(), msg.getIbuttonId());
            redissonEquipmentStore.putInRedisForIButtonStatus(msg.getUnitId(),IbuttonStatusEnum.REMOVED);
            //Insert log
            equipmentFault.setAlertType("Event");
            equipmentFault.setAlertType("IButton Removed");
            insertFaultLog(equipmentFault);
        }else if (EventIDEnum.MAIN_POWER_LOW_EVENT.getEventId().equals(eventId)){
            log.info("MAIN_POWER_LOW_EVENT, device:{}", msg.getUnitId());
            equipmentFault.setAlertType("Event");
            equipmentFault.setAlertType("Main power low event");
        }

        //handle iButton
        if(EventIDEnum.TRACK_POSITION_DATA.getEventId().equals(eventId)){
            if(StringUtils.isNotBlank(msg.getIbuttonId())){
                log.debug("IButton in attached status, device:{}, iButtonId:{}", msg.getUnitId(), msg.getIbuttonId());
                redissonEquipmentStore.putInRedisForIButtonStatus(msg.getUnitId(), IbuttonStatusEnum.ATTACHED);
                redissonEquipmentStore.putInRedisForIButtonId(msg.getUnitId(), msg.getIbuttonId());
            }else{
                log.debug("IButton in removed status, device:{}, iButtonId:{}", msg.getUnitId(), msg.getIbuttonId());
                redissonEquipmentStore.putInRedisForIButtonStatus(msg.getUnitId(),IbuttonStatusEnum.REMOVED);
            }
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
        if(speed > 0 && !VehicleStatusEnum.RUNNING.getCode().equals(currentDeviceStatus.getCode())){
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
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void insertEventLog(EquipmentOperationRecord equipmentOperationRecord){
        if(equipmentOperationRecord == null){
            return;
        }
        log.debug("InsertEventLog, equipmentOperationRecord :{}", equipmentOperationRecord);
        equipmentOperationRecordRepository.save(equipmentOperationRecord);
    }

    /**
     *
     * @param deviceNo
     * @param eventType
     * @param eventDesc
     * @param operationResult
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void insertEventLog(String deviceNo, EventCategory eventType, EventType eventDesc, OperationResult operationResult){
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(deviceNo);
        if (!equipment.isPresent()) {
            log.warn("Refused to insertEventLog, equipment not maintained yet, imei: {}", deviceNo);
            return;
        } else if (equipment.get().getVehicle() == null) {
            log.warn("Refused to insertEventLog, vehicle not bound yet, imei: {}", deviceNo);
            return;
        }
        EquipmentOperationRecord equipmentOperationRecord = new EquipmentOperationRecord();
        equipmentOperationRecord.setOperationSourceType(OperationSourceType.PLATFORM);
        equipmentOperationRecord.setEventType(eventType);
        equipmentOperationRecord.setEventDesc(eventDesc);
        equipmentOperationRecord.setResult(operationResult);
        equipmentOperationRecord.setEquipment(equipment.get());
        equipmentOperationRecord.setVehicle(equipment.get().getVehicle());
        log.debug("InsertEventLog, equipmentOperationRecord :{}", equipmentOperationRecord);
        equipmentOperationRecordRepository.save(equipmentOperationRecord);
    }

    /**
     *
     * @param deviceNo
     * @param operationSourceType
     * @param eventType
     * @param eventDesc
     * @param operationResult
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void insertEventLog(String deviceNo, OperationSourceType operationSourceType,EventCategory eventType, EventType eventDesc, OperationResult operationResult){
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(deviceNo);
        if (!equipment.isPresent()) {
            log.warn("Refused to insertEventLog, equipment not maintained yet, imei: {}", deviceNo);
            return;
        } else if (equipment.get().getVehicle() == null) {
            log.warn("Refused to insertEventLog, vehicle not bound yet, imei: {}", deviceNo);
            return;
        }
        EquipmentOperationRecord equipmentOperationRecord = new EquipmentOperationRecord();
        equipmentOperationRecord.setOperationSourceType(operationSourceType);
        equipmentOperationRecord.setEventType(eventType);
        equipmentOperationRecord.setEventDesc(eventDesc);
        equipmentOperationRecord.setResult(operationResult);
        equipmentOperationRecord.setEquipment(equipment.get());
        equipmentOperationRecord.setVehicle(equipment.get().getVehicle());
        log.debug("InsertEventLog, equipmentOperationRecord :{}", equipmentOperationRecord);
        equipmentOperationRecordRepository.save(equipmentOperationRecord);
    }

    /**
     *
     * @param equipmentFault
     */
    @Transactional
    public void insertFaultLog(EquipmentFault equipmentFault){
        if(equipmentFault == null){
            return;
        }
        equipmentFaultRepository.save(equipmentFault);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(String deviceNo, boolean isOnLine, boolean isMoving) {
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(deviceNo);
        if (!equipment.isPresent()) {
            log.warn("Refused to update status, equipment not maintained yet, imei: {}", deviceNo);
            return;
        } else if (equipment.get().getVehicle() == null) {
            log.warn("Refused to update status, vehicle not bound yet, imei: {}", deviceNo);
            return;
        }
        log.debug("In UpdateStatus, request for update equipment and vehicle , deviceNo: {},  online: {},  isMoving: {}", deviceNo,isOnLine,isMoving);
        Equipment ept = equipment.get();
        ept.setOnline(isOnLine);
        ept.getVehicle().setIsMoving(isMoving);
        equipmentRepository.save(ept);
    }

    /**
     *
     * @param deviceNo
     * @param bleName
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateBleName(String deviceNo, String bleName) {
        if(StringUtils.isBlank(deviceNo) || StringUtils.isBlank(bleName)){
            return;
        }
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(deviceNo);
        if (!equipment.isPresent()) {
            log.warn("Refused to update blueName, equipment not maintained yet, imei: {}", deviceNo);
            return;
        } else if (equipment.get().getVehicle() == null) {
            log.warn("Refused to update blueName, vehicle not bound yet, imei: {}", deviceNo);
            return;
        }
        log.debug("In updateBleName, request for update bluetooth name , deviceNo: {},  bleName: {},  isMoving: {}", deviceNo,bleName);
        Equipment ept = equipment.get();
        ept.setBluetoothName(bleName);
        equipmentRepository.save(ept);
    }

    @Transactional(readOnly = true)
    public Optional<VehicleMaintenance> findLatestVehicleMaintenance(Long vehicleId){
        log.debug("In findLatestVehicleMaintenance, request for get latest vehicle maintenance, vehicleId: {}", vehicleId);
        return vehicleMaintenanceRepository.findFirstByVehicle_IdOrderByCreatedDateDesc(vehicleId);
    }

    /**
     *
     * @param vehicleId
     * @return
     */
    @Transactional(readOnly = true)
    public VehicleCalcInfoResponseItemDTO getVehicleMileageAndFuelCalc(Long vehicleId) {
        log.debug("Call getVehicleMileageAndFuelCalc by VehicleId : {}", vehicleId);
        VehicleCalcInfoResponseItemDTO data = new VehicleCalcInfoResponseItemDTO();
        try {
            //最新一次的车辆维护信息
            Optional<VehicleMaintenance> latestVehicleMaintenance = this.findLatestVehicleMaintenance(vehicleId);
            BigDecimal maintainedFuel = BigDecimal.ZERO;
            BigDecimal maintainedMileage = BigDecimal.ZERO;
            Instant latestMaintenanceTime = Instant.EPOCH;
            if(latestVehicleMaintenance.isPresent()){
                maintainedFuel = latestVehicleMaintenance.get().getFuel();
                maintainedMileage = latestVehicleMaintenance.get().getMileage();
                latestMaintenanceTime = latestVehicleMaintenance.get().getLastModifiedDate();
            }else{
                Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
                if(vehicle.isPresent()){
                    maintainedFuel = vehicle.get().getTankVolume() != null ?vehicle.get().getTankVolume() : BigDecimal.ZERO;
                }else{
                    String message = "Vehicle not maintained yet, vehicleId:" + vehicleId;
                    log.debug(message);
                    return data;
                }
            }

            //获取车辆维护后的油耗和里程数
            QueryParams<VehicleTrajectoryDetails> queryParams = new QueryParams<>();
            queryParams.setDistinct(true);
            if (vehicleId != null) {
                queryParams.and(new Filter("vehicleTrajectory.vehicle.id", Filter.Operator.eq, vehicleId));
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
            BigDecimal remainFuelConsumptionInLiter = maintainedFuel.subtract(totalFuelConsumption);
            if(remainFuelConsumptionInLiter.compareTo(BigDecimal.ZERO) == 1){
                data.setRemainFuelConsumptionInLiter(remainFuelConsumptionInLiter);
            }else{
                data.setRemainFuelConsumptionInLiter(BigDecimal.ZERO);
            }
            data.setCurrentMileageInKM(maintainedMileage.add(totalMileage));
            log.debug("End process vehicle mileage and fuel calc: {}", data.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return data;
    }
}
