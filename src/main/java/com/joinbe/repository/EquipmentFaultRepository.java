package com.joinbe.repository;

import com.joinbe.domain.EquipmentFault;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the EquipmentFault entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EquipmentFaultRepository extends JpaRepository<EquipmentFault, Long>,
    JpaSpecificationExecutor<EquipmentFault> {

}
