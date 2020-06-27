package com.joinbe.repository;

import com.joinbe.domain.Equipment;
import com.joinbe.domain.enumeration.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Equipment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long>,
    JpaSpecificationExecutor<Equipment> {

    Optional<Equipment> findOneByImei(String imei);

    Optional<Equipment> findOneByImeiAndStatusNot(String imei, EquipmentStatus status);

    Optional<Equipment> findOneByIdentifyNumberAndStatusNot(String identifyNumber, EquipmentStatus status);

    List<Equipment> findAllByStatus(EquipmentStatus status);

}
