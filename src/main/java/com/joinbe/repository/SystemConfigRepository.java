package com.joinbe.repository;

import com.joinbe.domain.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the SystemConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long>,
    JpaSpecificationExecutor<SystemConfig> {

    SystemConfig findByKeyAndMerchantId(String key, Long merchantId);

    List<SystemConfig> findAllByKey(String key);
}
