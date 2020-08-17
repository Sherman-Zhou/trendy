package com.joinbe.repository;

import com.joinbe.domain.VehicleTrajectoryBackupFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the VehicleTrajectoryBackupFile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleTrajectoryBackupFileRepository extends JpaRepository<VehicleTrajectoryBackupFile, Long>,
    JpaSpecificationExecutor<VehicleTrajectoryBackupFile> {

    List<VehicleTrajectoryBackupFile> getAllByMerchantId(Long merchantId);

}


