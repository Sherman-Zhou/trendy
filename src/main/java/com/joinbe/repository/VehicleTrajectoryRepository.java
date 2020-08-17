package com.joinbe.repository;

import com.joinbe.domain.VehicleTrajectory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Spring Data  repository for the VehicleTrajectory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleTrajectoryRepository extends JpaRepository<VehicleTrajectory, Long>,
    JpaSpecificationExecutor<VehicleTrajectory> {

    Optional<VehicleTrajectory> findOneByTrajectoryId(String trajectoryId);

    List<VehicleTrajectory> findByVehicleIdOrderByTrajectoryIdDesc(Long vehicleId);


    @Query("select distinct trajectory from VehicleTrajectory trajectory left join fetch trajectory.details " +
        "where trajectory.endTime between :startTime and :endTime order by trajectory.trajectoryId, trajectory.startTime")
    List<VehicleTrajectory> findTrajectoryAfter(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);


    @Query("select t from VehicleTrajectory t where t.vehicle.merchant.id =:merchantId and t.endTime <= :endTime")
    Stream<VehicleTrajectory> findOldTrajectory(@Param("merchantId") Long merchantId, @Param("endTime") Instant endTime);
}
