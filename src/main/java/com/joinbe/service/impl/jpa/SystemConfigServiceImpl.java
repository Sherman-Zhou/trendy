package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.SystemConfig;
import com.joinbe.repository.SystemConfigRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.security.UserLoginInfo;
import com.joinbe.service.SystemConfigService;
import com.joinbe.service.dto.SystemConfigDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link SystemConfig}.
 */
@Service("JpaSystemConfigService")
@Transactional
public class SystemConfigServiceImpl implements SystemConfigService {

    private final Logger log = LoggerFactory.getLogger(SystemConfigServiceImpl.class);

    private final SystemConfigRepository systemConfigRepository;


    public SystemConfigServiceImpl(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }

    /**
     * Save a systemConfig.
     *
     * @param systemConfigDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public SystemConfigDTO save(SystemConfigDTO systemConfigDTO) {
        log.debug("Request to save SystemConfig : {}", systemConfigDTO);
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        SystemConfig config = systemConfigRepository.findByKeyAndMerchantId(SystemConfig.TRAJECTORY_RESERVE_DAYS, loginInfo.getMerchantId());
        config.setValue(String.valueOf(systemConfigDTO.getTrajectoryReserveDays()));
        SystemConfig lastBackupTime = systemConfigRepository.findByKeyAndMerchantId(SystemConfig.LAST_BACKUP_TIME, loginInfo.getMerchantId());

        if (StringUtils.isNotEmpty(systemConfigDTO.getLastBackupTime())) {
            lastBackupTime.setValue(systemConfigDTO.getLastBackupTime());
        }
        systemConfigDTO.setLastBackupTime(lastBackupTime.getValue());
        SystemConfig mileageMultiple = systemConfigRepository.findByKeyAndMerchantId(SystemConfig.MILEAGE_MULTIPLE, loginInfo.getMerchantId());
        mileageMultiple.setValue(systemConfigDTO.getMileageMultiple() != null ? systemConfigDTO.getMileageMultiple().toString() : null);
        return systemConfigDTO;
    }

    /**
     * Get all the systemConfigs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SystemConfigDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SystemConfigs");
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        QueryParams<SystemConfig> queryParams = new QueryParams<>();
        queryParams.and("merchant.id", Filter.Operator.eq, loginInfo.getMerchantId());
        return systemConfigRepository.findAll(queryParams, pageable)
            .map(SystemConfigService::toDto);
    }

    /**
     * Get one systemConfig by id.
     *
      * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public SystemConfigDTO find() {
        log.debug("Request to get SystemConfig"  );
        Map<String, String> configs = systemConfigRepository.findAll()
            .stream().collect(Collectors.toMap(SystemConfig::getKey, SystemConfig::getValue));
        SystemConfigDTO dto = new SystemConfigDTO();
        dto.setLastBackupTime(configs.get(SystemConfig.LAST_BACKUP_TIME));
        dto.setTrajectoryReserveDays(Long.parseLong(configs.get(SystemConfig.TRAJECTORY_RESERVE_DAYS)));
        dto.setMileageMultiple(new BigDecimal(configs.get(SystemConfig.MILEAGE_MULTIPLE)));
        return dto;
    }

    /**
     * Delete the systemConfig by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete SystemConfig : {}", id);
        systemConfigRepository.deleteById(id);
    }
}
