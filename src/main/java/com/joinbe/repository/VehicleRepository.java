package com.joinbe.repository;

import com.joinbe.domain.Vehicle;
import com.joinbe.domain.enumeration.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Vehicle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    List<Vehicle> findByShopIdAndStatus(String shopId, RecordStatus status);

    Optional<Vehicle> findOneByLicensePlateNumberAndStatus(String licensePlateNumber, RecordStatus status);
}
