package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.EquipmentFault;
import com.joinbe.service.dto.EquipmentFaultDTO;
import com.joinbe.web.rest.vm.EquipmentFaultVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.EquipmentFault}.
 */
public interface EquipmentFaultService {

    static EquipmentFaultDTO toDto(EquipmentFault equipmentFault) {

        return BeanConverter.toDto(equipmentFault, EquipmentFaultDTO.class);
    }

    static EquipmentFault toEntity(EquipmentFaultDTO equipmentFaultDTO) {

        return BeanConverter.toEntity(equipmentFaultDTO, EquipmentFault.class);
    }

    /**
     * Save a equipmentFault.
     *
     * @param equipmentFaultDTO the entity to save.
     * @return the persisted entity.
     */
    EquipmentFaultDTO save(EquipmentFaultDTO equipmentFaultDTO);

    Optional<EquipmentFaultDTO> read(Long id);

    void batchRead(List<Long> ids);

    /**
     * Get all the equipmentFaults.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EquipmentFaultDTO> findAll(Pageable pageable, EquipmentFaultVM vm);

    /**
     * Get the "id" equipmentFault.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EquipmentFaultDTO> findOne(Long id);

    /**
     * Delete the "id" equipmentFault.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
