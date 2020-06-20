package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.SystemConfig;
import com.joinbe.service.dto.SystemConfigDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link SystemConfig}.
 */
public interface SystemConfigService {

    static SystemConfigDTO toDto(SystemConfig systemConfig) {

        return BeanConverter.toDto(systemConfig, SystemConfigDTO.class);
    }

    static SystemConfig toEntity(SystemConfigDTO systemConfigDTO) {

        return BeanConverter.toEntity(systemConfigDTO, SystemConfig.class);
    }

    /**
     * Save a systemConfig.
     *
     * @param systemConfigDTO the entity to save.
     * @return the persisted entity.
     */
    SystemConfigDTO save(SystemConfigDTO systemConfigDTO);

    /**
     * Get all the systemConfigs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SystemConfigDTO> findAll(Pageable pageable);

    /**
     * Get the systemConfig.
     *
     * @return the entity.
     */
    SystemConfigDTO find();

    /**
     * Delete the "id" systemConfig.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
