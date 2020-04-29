package com.joinbe.repository;

import com.joinbe.domain.VehicleTrajectoryDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the VehicleTrajectoryDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleTrajectoryDetailsRepository extends JpaRepository<VehicleTrajectoryDetails, Long>,
    JpaSpecificationExecutor<VehicleTrajectoryDetails> {

}
