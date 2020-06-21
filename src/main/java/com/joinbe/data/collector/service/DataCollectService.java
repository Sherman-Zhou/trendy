package com.joinbe.data.collector.service;

import com.joinbe.data.collector.netty.protocol.PositionProtocol;
import com.joinbe.data.collector.redistore.RedissonEquipmentStore;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.VehicleTrajectory;
import com.joinbe.domain.VehicleTrajectoryDetails;
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
import java.util.Optional;

/**
 * Service for handle data
 */
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
        //TODO - Generate trajectory
        VehicleTrajectory vehicleTrajectory = new VehicleTrajectory();
        vehicleTrajectory.setTrajectoryId(redissonEquipmentStore.genId());
        vehicleTrajectory.setStartLongitude(BigDecimal.valueOf(msg.getLongitude()));
        vehicleTrajectory.setStartLatitude(BigDecimal.valueOf(msg.getLatitude()));
        vehicleTrajectory.setStartTime(Instant.now());
        vehicleTrajectory.setEquipment(equipment.get());
        vehicleTrajectory.setVehicle(equipment.get().getVehicle());

        //Generate trajectory detail
        VehicleTrajectoryDetails vehicleTrajectoryDetails = new VehicleTrajectoryDetails();
        vehicleTrajectoryDetails.setReceivedTime(Instant.now());
        vehicleTrajectoryDetails.setLongitude(BigDecimal.valueOf(msg.getLongitude()));
        vehicleTrajectoryDetails.setLatitude(BigDecimal.valueOf(msg.getLatitude()));
        vehicleTrajectoryDetails.setActualSpeed(BigDecimal.valueOf(msg.getSpeed()));
        vehicleTrajectoryDetails.setMileage(BigDecimal.valueOf(msg.getMileage()));
        vehicleTrajectoryDetails.setVehicleTrajectory(vehicleTrajectory);

        vehicleTrajectoryRepository.save(vehicleTrajectory);
    }

}
