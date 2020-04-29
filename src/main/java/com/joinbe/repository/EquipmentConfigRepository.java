package com.joinbe.repository;

import com.joinbe.domain.EquipmentConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the EquipmentConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EquipmentConfigRepository extends JpaRepository<EquipmentConfig, Long>,
    JpaSpecificationExecutor<EquipmentConfig> {

}
