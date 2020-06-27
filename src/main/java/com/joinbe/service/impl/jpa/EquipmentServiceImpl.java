package com.joinbe.service.impl.jpa;

import com.joinbe.common.excel.EquipmentData;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Division;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.Vehicle;
import com.joinbe.domain.enumeration.EquipmentStatus;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.DivisionRepository;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.dto.EquipmentDTO;
import com.joinbe.service.dto.UploadResultDTO;
import com.joinbe.web.rest.vm.EquipmentVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Equipment}.
 */
@Service("JpaEquipmentService")
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private final Logger log = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentRepository equipmentRepository;

    private final DivisionRepository divisionRepository;

    private final MessageSource messageSource;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, DivisionRepository divisionRepository, MessageSource messageSource) {
        this.equipmentRepository = equipmentRepository;
        this.divisionRepository = divisionRepository;
        this.messageSource = messageSource;
    }

    /**
     * Save a equipment.
     *
     * @param equipmentDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EquipmentDTO save(EquipmentDTO equipmentDTO) {
        log.debug("Request to save Equipment : {}", equipmentDTO);
        Equipment equipment = EquipmentService.toEntity(equipmentDTO);
        equipment = equipmentRepository.save(equipment);
        return EquipmentService.toDto(equipment);
    }

    /**
     * Get all the equipment.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentDTO> findAll(Pageable pageable, EquipmentVM vm) {
        log.debug("Request to get all Equipment");
        QueryParams<Equipment> queryParams = new QueryParams<>();
        if (StringUtils.isNotEmpty(vm.getIdentifyNumber())) {
            queryParams.and("identifyNumber", Filter.Operator.like, vm.getIdentifyNumber());
        }
        if (StringUtils.isNotEmpty(vm.getImei())) {
            queryParams.and("imei", Filter.Operator.like, vm.getImei());
        }
        if (StringUtils.isNotEmpty(vm.getSimCardNum())) {
            queryParams.and("simCardNum", Filter.Operator.like, vm.getSimCardNum());
        }
        if(vm.getIsOnline() != null){
            queryParams.and("isOnline", Filter.Operator.eq, vm.getIsOnline());
        }

        if (vm.getIsBounded() != null) {
            queryParams.and("vehicle", vm.getIsBounded() ? Filter.Operator.isNotNull : Filter.Operator.isNull, null);
        }
        return equipmentRepository.findAll(queryParams, pageable)
            .map(EquipmentService::toDto);
    }

    /**
     * Get one equipment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentDTO> findOne(Long id) {
        log.debug("Request to get Equipment : {}", id);
        return equipmentRepository.findById(id)
            .map(EquipmentService::toDto);
    }

    /**
     * Delete the equipment by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Equipment : {}", id);
        Optional<Equipment> equipmentOptional = equipmentRepository.findById(id);
        if (equipmentOptional.isPresent()) {
            Equipment equipment = equipmentOptional.get();
            equipment.setStatus(EquipmentStatus.DELETED);
            equipment.setVehicle(null);
            Vehicle vehicle = equipment.getVehicle();
            if (vehicle != null) {
                vehicle.setBounded(false);
            }
        }
//        equipmentRepository.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentDTO> findByLicensePlateNumber(String plateNumber) {
        log.debug("Request to get all Equipment by plateNumber: {}", plateNumber);
        if(StringUtils.isBlank(plateNumber)){
            return Optional.empty();
        }
        QueryParams<Equipment> queryParams = new QueryParams<>();
        queryParams.and("vehicle.licensePlateNumber", Filter.Operator.eq, plateNumber);
        return equipmentRepository.findOne(queryParams).map(EquipmentService::toDto);
    }

    @Override
    public List<EquipmentDTO> findAllUnboundEquipments() {
        return equipmentRepository.findAllByStatus(EquipmentStatus.UNBOUND)
            .stream().map(EquipmentService::toDto).collect(Collectors.toList());
    }

    @Override
    public List<UploadResultDTO> upload(List<EquipmentData> records) {
        List<UploadResultDTO> results = new ArrayList<>();
        List<Equipment> equipments = new ArrayList<>();

        for (EquipmentData equipmentData : records) {
            boolean hasError = false;
            Division org = null;
            if (equipmentRepository.findOneByImeiAndStatusNot(equipmentData.getImei(), EquipmentStatus.DELETED).isPresent()) {
                createResult("equipment.upload.imei.exists", equipmentData.getRowIdx(), false, results);
                hasError = true;
            }
            if (equipmentRepository.findOneByIdentifyNumberAndStatusNot(equipmentData.getIdentifyNumber(), EquipmentStatus.DELETED).isPresent()) {
                createResult("equipment.upload.equipmentId.exists", equipmentData.getRowIdx(), false, results);
                hasError = true;
            }
            Optional<Division> divisionOptional = divisionRepository.findByNameAndStatus(equipmentData.getDivName(), RecordStatus.ACTIVE);
            if (divisionOptional.isPresent()) {
                List<Division> orgs = divisionOptional.get().getChildren().stream()
                    .filter(division -> equipmentData.getOrgName().equalsIgnoreCase(division.getName()))
                    .filter(division -> RecordStatus.ACTIVE.equals(division.getStatus()))
                    .collect(Collectors.toList());
                if (orgs.isEmpty()) {
                    createResult("equipment.upload.division.not.exists", equipmentData.getRowIdx(), false, results);
                    hasError = true;
                } else {
                    org = orgs.get(0);
                }
            } else {
                createResult("equipment.upload.division.not.exists", equipmentData.getRowIdx(), false, results);
                hasError = true;
            }
            if (!hasError) {
                // createResult("excel.upload.success", equipmentData.getRowIdx(), true, results);
                Equipment equipment = new Equipment();
                equipment.setIdentifyNumber(equipmentData.getIdentifyNumber());
                equipment.setImei(equipmentData.getImei());
                equipment.setSimCardNum(equipmentData.getSimCardNum());
                equipment.setVersion(equipmentData.getVersion());
                equipment.setRemark(equipmentData.getRemark());
                equipment.setOnline(false);
                equipment.setStatus(EquipmentStatus.UNBOUND);
                equipment.setDivision(org);
                equipments.add(equipment);
            }
        }
        equipmentRepository.saveAll(equipments);
        return results;
    }

    private void createResult(String msgKey, int rowIdx, boolean success, List<UploadResultDTO> results) {
        String message = messageSource.getMessage(msgKey, new String[]{String.valueOf(rowIdx)}, LocaleContextHolder.getLocale());
        UploadResultDTO result = new UploadResultDTO();
        result.setIsSuccess(success);
        result.setMsg(message);
        result.setRowNum((long) rowIdx);
        results.add(result);
    }

}
