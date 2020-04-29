package com.joinbe.service.impl.jpa;

import com.joinbe.domain.EquipmentOperationRecord;
import com.joinbe.repository.EquipmentOperationRecordRepository;
import com.joinbe.service.EquipmentOperationRecordService;
import com.joinbe.service.dto.EquipmentOperationRecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link EquipmentOperationRecord}.
 */
@Service("JpaEquipmentOperationRecordService")
@Transactional
public class EquipmentOperationRecordServiceImpl implements EquipmentOperationRecordService {

    private final Logger log = LoggerFactory.getLogger(EquipmentOperationRecordServiceImpl.class);

    private final EquipmentOperationRecordRepository equipmentOperationRecordRepository;


    public EquipmentOperationRecordServiceImpl(EquipmentOperationRecordRepository equipmentOperationRecordRepository) {
        this.equipmentOperationRecordRepository = equipmentOperationRecordRepository;
    }

    /**
     * Save a equipmentOperationRecord.
     *
     * @param equipmentOperationRecordDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EquipmentOperationRecordDTO save(EquipmentOperationRecordDTO equipmentOperationRecordDTO) {
        log.debug("Request to save EquipmentOperationRecord : {}", equipmentOperationRecordDTO);
        EquipmentOperationRecord equipmentOperationRecord = EquipmentOperationRecordService.toEntity(equipmentOperationRecordDTO);
        equipmentOperationRecord = equipmentOperationRecordRepository.save(equipmentOperationRecord);
        return EquipmentOperationRecordService.toDto(equipmentOperationRecord);
    }

    /**
     * Get all the equipmentOperationRecords.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentOperationRecordDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EquipmentOperationRecords");
        return equipmentOperationRecordRepository.findAll(pageable)
            .map(EquipmentOperationRecordService::toDto);
    }

    /**
     * Get one equipmentOperationRecord by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentOperationRecordDTO> findOne(Long id) {
        log.debug("Request to get EquipmentOperationRecord : {}", id);
        return equipmentOperationRecordRepository.findById(id)
            .map(EquipmentOperationRecordService::toDto);
    }

    /**
     * Delete the equipmentOperationRecord by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EquipmentOperationRecord : {}", id);
        equipmentOperationRecordRepository.deleteById(id);
    }
}
