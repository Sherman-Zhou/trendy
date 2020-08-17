package com.joinbe.repository;

import com.joinbe.domain.VehicleTrajectoryDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the VehicleTrajectoryDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleTrajectoryDetailsRepository extends JpaRepository<VehicleTrajectoryDetails, Long>,
    JpaSpecificationExecutor<VehicleTrajectoryDetails> {

    @Modifying
    @Query(nativeQuery = true, value = "delete from vehicle_trajectory_details where vehicle_trajectory_id =:trajectoryId")
    void deleteByVehicleTrajectoryId(@Param("trajectoryId") Long trajectoryId);

}
