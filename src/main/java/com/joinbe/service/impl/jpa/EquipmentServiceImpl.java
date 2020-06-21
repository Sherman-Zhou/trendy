package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.Vehicle;
import com.joinbe.domain.enumeration.EquipmentStatus;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.dto.EquipmentDTO;
import com.joinbe.web.rest.vm.EquipmentVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public EquipmentServiceImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
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

}
