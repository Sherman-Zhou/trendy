package com.joinbe.repository;

import com.joinbe.domain.VehicleTrajectory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the VehicleTrajectory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleTrajectoryRepository extends JpaRepository<VehicleTrajectory, Long>,
    JpaSpecificationExecutor<VehicleTrajectory> {

    List<VehicleTrajectory> findByVehicleId(Long vehicleId);

}
