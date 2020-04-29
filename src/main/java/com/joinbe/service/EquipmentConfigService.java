package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.EquipmentConfig;
import com.joinbe.service.dto.EquipmentConfigDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.EquipmentConfig}.
 */
public interface EquipmentConfigService {

    static EquipmentConfigDTO toDto(EquipmentConfig equipmentConfig) {

        return BeanConverter.toDto(equipmentConfig, EquipmentConfigDTO.class);
    }

    static EquipmentConfig toEntity(EquipmentConfigDTO equipmentConfigDTO) {

        return BeanConverter.toEntity(equipmentConfigDTO, EquipmentConfig.class);
    }

    /**
     * Save a equipmentConfig.
     *
     * @param equipmentConfigDTO the entity to save.
     * @return the persisted entity.
     */
    EquipmentConfigDTO save(EquipmentConfigDTO equipmentConfigDTO);

    /**
     * Get all the equipmentConfigs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EquipmentConfigDTO> findAll(Pageable pageable);

    /**
     * Get the "id" equipmentConfig.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EquipmentConfigDTO> findOne(Long id);

    /**
     * Delete the "id" equipmentConfig.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
