package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.enumeration.EquipmentStatus;
import com.joinbe.service.dto.EquipmentDTO;
import com.joinbe.web.rest.vm.EquipmentVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.Equipment}.
 */
public interface EquipmentService {

    static EquipmentDTO toDto(Equipment equipment) {
        EquipmentDTO dto = BeanConverter.toDto(equipment, EquipmentDTO.class);
        dto.setStatus(EquipmentStatus.getCode(equipment.getStatus()));
        return dto;
    }

    static Equipment toEntity(EquipmentDTO equipmentDTO) {

        Equipment dto = BeanConverter.toEntity(equipmentDTO, Equipment.class);
        dto.setStatus(EquipmentStatus.resolve(equipmentDTO.getStatus()));
        return dto;
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
    Page<EquipmentDTO> findAll(Pageable pageable, EquipmentVM vm);

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

    /***
     * get equipment by plate number
     * @param plateNumber
     * @return
     */
    Optional<EquipmentDTO> findByLicensePlateNumber(String plateNumber);

    List<EquipmentDTO> findAllUnboundEquipments() ;
}
