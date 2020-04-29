package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.Equipment;
import com.joinbe.service.dto.EquipmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.Equipment}.
 */
public interface EquipmentService {

    static EquipmentDTO toDto(Equipment equipment) {

        return BeanConverter.toDto(equipment, EquipmentDTO.class);
    }

    static Equipment toEntity(EquipmentDTO equipmentDTO) {

        return BeanConverter.toEntity(equipmentDTO, Equipment.class);
    }

    /**
     * Save a equipment.
     *
     * @param equipmentDTO the entity to save.
     * @return the persisted entity.
     */
    EquipmentDTO save(EquipmentDTO equipmentDTO);

    /**
     * Get all the equipment.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EquipmentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" equipment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EquipmentDTO> findOne(Long id);

    /**
     * Delete the "id" equipment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
