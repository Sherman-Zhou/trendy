package com.joinbe.repository;

import com.joinbe.domain.EquipmentOperationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the EquipmentOperationRecord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EquipmentOperationRecordRepository extends JpaRepository<EquipmentOperationRecord, Long>,
    JpaSpecificationExecutor<EquipmentOperationRecord> {

}
