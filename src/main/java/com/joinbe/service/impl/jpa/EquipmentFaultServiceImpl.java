package com.joinbe.service.impl.jpa;

import com.joinbe.domain.EquipmentFault;
import com.joinbe.repository.EquipmentFaultRepository;
import com.joinbe.service.EquipmentFaultService;
import com.joinbe.service.dto.EquipmentFaultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link EquipmentFault}.
 */
@Service("JpaEquipmentFaultService")
@Transactional
public class EquipmentFaultServiceImpl implements EquipmentFaultService {

    private final Logger log = LoggerFactory.getLogger(EquipmentFaultServiceImpl.class);

    private final EquipmentFaultRepository equipmentFaultRepository;

    public EquipmentFaultServiceImpl(EquipmentFaultRepository equipmentFaultRepository) {
        this.equipmentFaultRepository = equipmentFaultRepository;
    }

    /**
     * Save a equipmentFault.
     *
     * @param equipmentFaultDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EquipmentFaultDTO save(EquipmentFaultDTO equipmentFaultDTO) {
        log.debug("Request to save EquipmentFault : {}", equipmentFaultDTO);
        EquipmentFault equipmentFault = EquipmentFaultService.toEntity(equipmentFaultDTO);
        equipmentFault = equipmentFaultRepository.save(equipmentFault);
        return EquipmentFaultService.toDto(equipmentFault);
    }

    /**
     * Get all the equipmentFaults.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentFaultDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EquipmentFaults");
        return equipmentFaultRepository.findAll(pageable)
            .map(EquipmentFaultService::toDto);
    }

    /**
     * Get one equipmentFault by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentFaultDTO> findOne(Long id) {
        log.debug("Request to get EquipmentFault : {}", id);
        return equipmentFaultRepository.findById(id)
            .map(EquipmentFaultService::toDto);
    }

    /**
     * Delete the equipmentFault by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EquipmentFault : {}", id);
        equipmentFaultRepository.deleteById(id);
    }
}
