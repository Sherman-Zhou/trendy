package com.joinbe.data.collector.service;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.config.Constants;
import com.joinbe.data.collector.netty.protocol.code.EventIDEnum;
import com.joinbe.data.collector.netty.protocol.message.PositionProtocol;
import com.joinbe.data.collector.service.dto.VehicleCalcInfoResponseItemDTO;
import com.joinbe.data.collector.store.RedissonEquipmentStore;
import com.joinbe.domain.*;
import com.joinbe.domain.enumeration.*;
import com.joinbe.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataCollectService {
    private final Logger log = LoggerFactory.getLogger(DataCollectService.class);
    public static final String CMD_END = "\r\n";
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
        //??????event
        handleEvent(msg, equipment.get());
        //????????????
        VehicleStatusEnum previousDeviceStatus = redissonEquipmentStore.getDeviceStatus(msg.getUnitId());
        VehicleFireStatusEnum previousFireStatus = redissonEquipmentStore.getDeviceFireStatus(msg.getUnitId());
        //??????Input??????
        handleInputStatus(msg,equipment.get().getVehicle().getSignalInd());
        //??????????????????
        //handleRealTimeSpeed(msg);
        //????????????
        VehicleStatusEnum currentDeviceStatus = redissonEquipmentStore.getDeviceStatus(msg.getUnitId());
        VehicleFireStatusEnum currentFireStatus = redissonEquipmentStore.getDeviceFireStatus(msg.getUnitId());

        log.info("DeviceId: {}, previousDeviceStatus: {},  currentDeviceStatus: {}",msg.getUnitId(), previousDeviceStatus.getCode(), currentDeviceStatus.getCode());
        log.info("DeviceId: {}, previousFireStatus: {},  currentFireStatus: {}",msg.getUnitId(), previousFireStatus.getCode(), currentFireStatus.getCode());

        //??????????????????
        if(previousDeviceStatus == null || VehicleStatusEnum.UNKNOWN.equals(previousDeviceStatus) || !previousDeviceStatus.equals(currentDeviceStatus)){
            boolean isRunning = VehicleStatusEnum.RUNNING.equals(currentDeviceStatus);
            log.info("Vehicle's status is changed, update status, deviceNo: {}, isRunning: {}", msg.getUnitId(), isRunning);
            this.updateStatus(msg.getUnitId(),true,isRunning);
        }

        //???????????????????????????????????????????????????
        if(VehicleFireStatusEnum.UNKNOWN.equals(previousFireStatus)){
            log.info("Set unknown fire status to real pre fire status : {}", msg.getUnitId());
            previousFireStatus = redissonEquipmentStore.getDevicePreFireStatus(msg.getUnitId());
        }

        /**
         * ????????????????????????
         * ????????????????????? && ???????????????????????????
         */
        if(VehicleFireStatusEnum.CLOSE_FIRE.equals(currentFireStatus) && !VehicleFireStatusEnum.OPEN_FIRE.equals(previousFireStatus)){
            log.debug("Ignore the position data, imei: {}",msg.getUnitId());
            return;
        }

        /**
         * ????????????
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
        vehicleTrajectoryDetails.setVoltageInput2(msg.getAnalogInput2()!=null ? BigDecimal.valueOf(msg.getAnalogInput2()):null);

        /**
         * ?????????????????? ??????????????? && ??????????????????
         * ??????????????? ??????????????? && ??????????????????
         * ??????????????? ??????????????? && ???????????????
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
                //??????????????????????????????, ???0
                newVehicleTrajectory.setMileage(BigDecimal.ZERO);
                newVehicleTrajectory.setDetails(new HashSet<VehicleTrajectoryDetails>() {{ add(vehicleTrajectoryDetails);}});
                vehicleTrajectoryDetails.setVehicleTrajectory(newVehicleTrajectory);
                //?????????????????????
                redissonEquipmentStore.putInRedisForTrajectory(msg.getUnitId(), newTrajectoryId);
            }else if(existTrajectory != null){
                log.info("Use exist trajectory, {}", existTrajectoryId);
                existTrajectory.getDetails().add(vehicleTrajectoryDetails);
                //?????????????????????????????????
                BigDecimal mileage = handleTrajectoryMileage(existTrajectory);
                existTrajectory.setMileage(mileage);
                vehicleTrajectoryDetails.setVehicleTrajectory(existTrajectory);
            }
        }else if(existTrajectory != null){
                existTrajectory.setEndTime(Instant.now());
                //TODO - ??????????????????
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
    private void handleInputStatus(PositionProtocol msg,Long signalInd) {
        Integer inputStatus = msg.getInputStatus();
        if(msg == null ||inputStatus == null || StringUtils.isBlank(msg.getUnitId())){
            return;
        }
        /**
         * ?????????0,1,4,5
         * ?????????2,3,6,7
         */
        //0-???????????????1-????????????
        if(Long.valueOf("1").equals(signalInd)){
            if(inputStatus == 2 || inputStatus == 3 || inputStatus == 6 || inputStatus == 7){
                redissonEquipmentStore.putInRedisForDoorStatus(msg.getUnitId(), VehicleDoorStatusEnum.OPEN);
            }else{
                redissonEquipmentStore.putInRedisForDoorStatus(msg.getUnitId(), VehicleDoorStatusEnum.CLOSE);
            }
        }else{
            if(inputStatus == 0 || inputStatus == 1 || inputStatus == 4 || inputStatus == 5){
                redissonEquipmentStore.putInRedisForDoorStatus(msg.getUnitId(), VehicleDoorStatusEnum.OPEN);
            }else{
                redissonEquipmentStore.putInRedisForDoorStatus(msg.getUnitId(), VehicleDoorStatusEnum.CLOSE);
            }
        }

        /**
         * ?????????1,3,5,7
         * ?????????0,2,4,6
         */
        if(inputStatus == 1 || inputStatus == 3 || inputStatus == 5 || inputStatus == 7){
            redissonEquipmentStore.putInRedisForFireStatus(msg.getUnitId(), VehicleFireStatusEnum.OPEN_FIRE);
            //??????: ???????????? = ????????????
            redissonEquipmentStore.putInRedisForStatus(msg.getUnitId(),VehicleStatusEnum.RUNNING);
        }else{
            redissonEquipmentStore.putInRedisForFireStatus(msg.getUnitId(), VehicleFireStatusEnum.CLOSE_FIRE);
            //??????: ???????????? = ????????????
            redissonEquipmentStore.putInRedisForStatus(msg.getUnitId(), VehicleStatusEnum.STOPPED);
        }
    }

    /**
     * ??????????????????
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
     * ??????Event
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
                log.info("IButton in attached status, device:{}, iButtonId:{}", msg.getUnitId(), msg.getIbuttonId());
                redissonEquipmentStore.putInRedisForIButtonStatus(msg.getUnitId(), IbuttonStatusEnum.ATTACHED);
                redissonEquipmentStore.putInRedisForIButtonId(msg.getUnitId(), msg.getIbuttonId());
            }else{
                log.info("IButton in removed status, device:{}, iButtonId:{}", msg.getUnitId(), msg.getIbuttonId());
                redissonEquipmentStore.putInRedisForIButtonStatus(msg.getUnitId(),IbuttonStatusEnum.REMOVED);
            }
        }
    }

    /**
     * ????????????????????????
     * ????????????????????? ??????3?????????????????????0???????????????????????? ???????????????
     * ????????????????????? ????????????????????????0????????????????????????0?????? ???????????? - ??????????????????0????????? > 3??????????????????
     */
    private void handleRealTimeSpeed(PositionProtocol msg) {
        if(msg.getSpeed() == null || StringUtils.isBlank(msg.getUnitId())){
            return;
        }
        int speed = msg.getSpeed().intValue();
        String unitId = msg.getUnitId();
        VehicleStatusEnum currentDeviceStatus = redissonEquipmentStore.getDeviceStatus(unitId);
        if(speed > 0 && !VehicleStatusEnum.RUNNING.getCode().equals(currentDeviceStatus.getCode())){
            //?????????????????? & ????????????0?????????
            redissonEquipmentStore.putInRedisForStatus(unitId,VehicleStatusEnum.RUNNING);
            redissonEquipmentStore.putInRedisForCalcStatus(unitId, System.currentTimeMillis());
        }else if(speed > 0){
            //????????????0?????????
            redissonEquipmentStore.putInRedisForCalcStatus(unitId, System.currentTimeMillis());
        }

        //?????????0??????????????? - ??????????????????0????????? > 3??????????????????
        if(speed == 0){
            //????????????????????????0?????????
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

    /**
     *
     * @param deviceNo
     * @param isOnLine
     * @param isMoving
     */
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
     * @param isOnLine
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(String deviceNo, boolean isOnLine) {
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(deviceNo);
        if (!equipment.isPresent()) {
            log.warn("Refused to update status, equipment not maintained yet, imei: {}", deviceNo);
            return;
        }
        log.info("In UpdateStatus, request for update equipment online status, deviceNo: {},  online: {}", deviceNo,isOnLine);
        Equipment ept = equipment.get();
        ept.setOnline(isOnLine);
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
        }
        log.info("In updateBleName, request for update bluetooth name , deviceNo: {},  bleName: {}", deviceNo,bleName);
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
            //?????????????????????????????????
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

            //??????????????????????????????????????????
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

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateDeviceMileage(String deviceNo, BigDecimal mileageOffset, BigDecimal mileageMultiple) {
        if(StringUtils.isBlank(deviceNo)){
            return;
        }
        Optional<Equipment> equipment = equipmentRepository.findOneByImei(deviceNo);
        if (!equipment.isPresent()) {
            log.error("Refused to update device mileage, equipment not maintained yet, imei: {}", deviceNo);
            return;
        }
        log.debug("In updateDeviceMileage, request for update device initial mileage , deviceNo: {},  mileageOffset: {},  mileageMultiple: {}", deviceNo,mileageOffset,mileageMultiple);
        Equipment ept = equipment.get();
        ept.setInitMileage(mileageOffset);
        ept.setMultipleMileage(mileageMultiple);
        equipmentRepository.save(ept);
    }
}
