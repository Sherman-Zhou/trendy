package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.EquipmentOperationRecord;
import com.joinbe.service.dto.EquipmentOperationRecordDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.EquipmentOperationRecord}.
 */
public interface EquipmentOperationRecordService {

    static EquipmentOperationRecordDTO toDto(EquipmentOperationRecord equipmentOperationRecord) {

        return BeanConverter.toDto(equipmentOperationRecord, EquipmentOperationRecordDTO.class);
    }

    static EquipmentOperationRecord toEntity(EquipmentOperationRecordDTO equipmentOperationRecordDTO) {

        return BeanConverter.toEntity(equipmentOperationRecordDTO, EquipmentOperationRecord.class);
    }

    /**
     * Save a equipmentOperationRecord.
     *
     * @param equipmentOperationRecordDTO the entity to save.
     * @return the persisted entity.
     */
    EquipmentOperationRecordDTO save(EquipmentOperationRecordDTO equipmentOperationRecordDTO);

    /**
     * Get all the equipmentOperationRecords.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EquipmentOperationRecordDTO> findAll(Pageable pageable);

    /**
     * Get the "id" equipmentOperationRecord.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EquipmentOperationRecordDTO> findOne(Long id);

    /**
     * Delete the "id" equipmentOperationRecord.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
