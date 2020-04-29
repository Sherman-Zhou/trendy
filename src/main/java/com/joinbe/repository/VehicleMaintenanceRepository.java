package com.joinbe.repository;

import com.joinbe.domain.VehicleMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the VehicleMaintenance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleMaintenanceRepository extends JpaRepository<VehicleMaintenance, Long>,
    JpaSpecificationExecutor<VehicleMaintenance> {

}
