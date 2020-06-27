package com.joinbe.service.impl.jpa;

import com.joinbe.domain.SystemConfig;
import com.joinbe.repository.SystemConfigRepository;
import com.joinbe.service.SystemConfigService;
import com.joinbe.service.dto.SystemConfigDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        SystemConfig config = systemConfigRepository.findByKey(SystemConfig.TRAJECTORY_RESERVE_DAYS);

        config.setValue(String.valueOf(systemConfigDTO.getTrajectoryReserveDays()));
        SystemConfig lastBackupTime = systemConfigRepository.findByKey(SystemConfig.LAST_BACKUP_TIME);
        if (StringUtils.isNotEmpty(systemConfigDTO.getLastBackupTime())) {
            lastBackupTime.setValue(systemConfigDTO.getLastBackupTime());
        }
        systemConfigDTO.setLastBackupTime(lastBackupTime.getValue());
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
        return systemConfigRepository.findAll(pageable)
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
