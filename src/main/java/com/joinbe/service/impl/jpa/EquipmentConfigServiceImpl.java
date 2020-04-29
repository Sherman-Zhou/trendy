package com.joinbe.service.impl.jpa;

import com.joinbe.domain.EquipmentConfig;
import com.joinbe.repository.EquipmentConfigRepository;
import com.joinbe.service.EquipmentConfigService;
import com.joinbe.service.dto.EquipmentConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link EquipmentConfig}.
 */
@Service("JpaEquipmentConfigService")
@Transactional
public class EquipmentConfigServiceImpl implements EquipmentConfigService {

    private final Logger log = LoggerFactory.getLogger(EquipmentConfigServiceImpl.class);

    private final EquipmentConfigRepository equipmentConfigRepository;


    public EquipmentConfigServiceImpl(EquipmentConfigRepository equipmentConfigRepository) {
        this.equipmentConfigRepository = equipmentConfigRepository;
    }

    /**
     * Save a equipmentConfig.
     *
     * @param equipmentConfigDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EquipmentConfigDTO save(EquipmentConfigDTO equipmentConfigDTO) {
        log.debug("Request to save EquipmentConfig : {}", equipmentConfigDTO);
        EquipmentConfig equipmentConfig = EquipmentConfigService.toEntity(equipmentConfigDTO);
        equipmentConfig = equipmentConfigRepository.save(equipmentConfig);
        return EquipmentConfigService.toDto(equipmentConfig);
    }

    /**
     * Get all the equipmentConfigs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentConfigDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EquipmentConfigs");
        return equipmentConfigRepository.findAll(pageable)
            .map(EquipmentConfigService::toDto);
    }

    /**
     * Get one equipmentConfig by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentConfigDTO> findOne(Long id) {
        log.debug("Request to get EquipmentConfig : {}", id);
        return equipmentConfigRepository.findById(id)
            .map(EquipmentConfigService::toDto);
    }

    /**
     * Delete the equipmentConfig by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EquipmentConfig : {}", id);
        equipmentConfigRepository.deleteById(id);
    }
}
