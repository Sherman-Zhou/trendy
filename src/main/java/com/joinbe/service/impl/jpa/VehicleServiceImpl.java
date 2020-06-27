package com.joinbe.service.impl.jpa;

import com.joinbe.common.excel.BindingData;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Division;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.Vehicle;
import com.joinbe.domain.enumeration.EquipmentStatus;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.DivisionRepository;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.VehicleService;
import com.joinbe.service.dto.UploadResultDTO;
import com.joinbe.service.dto.VehicleDetailsDTO;
import com.joinbe.service.dto.VehicleSummaryDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.EquipmentVehicleBindingVM;
import com.joinbe.web.rest.vm.VehicleBindingVM;
import com.joinbe.web.rest.vm.VehicleVM;
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
 * Service Implementation for managing {@link Vehicle}.
 */
@Service("JpaVehicleService")
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final Logger log = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository vehicleRepository;

    private final EquipmentRepository equipmentRepository;

    private final DivisionRepository divisionRepository;

    private final MessageSource messageSource;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, EquipmentRepository equipmentRepository,
                              DivisionRepository divisionRepository, MessageSource messageSource) {
        this.vehicleRepository = vehicleRepository;
        this.equipmentRepository = equipmentRepository;
        this.divisionRepository = divisionRepository;
        this.messageSource = messageSource;
    }

    /**
     * Save a vehicle.
     *
     * @param vehicleDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VehicleDetailsDTO save(VehicleDetailsDTO vehicleDetailsDTO) {
        log.debug("Request to save Vehicle : {}", vehicleDetailsDTO);
        Vehicle vehicle = VehicleService.toEntity(vehicleDetailsDTO);
        Long divisionId = vehicleDetailsDTO.getDivisionId();
        if (vehicleDetailsDTO.getId() != null) {
            divisionId = vehicleRepository.getOne(vehicleDetailsDTO.getId()).getDivision().getId();
        }
        Optional<Division> division = divisionRepository.findById(divisionId);
        if (division.isPresent()) {
            SecurityUtils.checkDataPermission(division.get());
            vehicle.setDivision(division.get());
        } else {
            throw new BadRequestAlertException("Division does not exist", "Vehicle", "divnotexists");
        }
        vehicle = vehicleRepository.save(vehicle);
        return VehicleService.toDto(vehicle);
    }

    /**
     * Get all the vehicles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VehicleDetailsDTO> findAll(Pageable pageable, VehicleVM vm) {
        log.debug("Request to get all Vehicles");
        QueryParams<Vehicle> queryParams = new QueryParams<>();
        if (vm.getDivisionId() != null) {
            SecurityUtils.checkDataPermission(vm.getDivisionId());
            queryParams.and("division.id", Filter.Operator.eq, vm.getDivisionId());
        } else {
            // add user's division condition
            List<Long> userDivisionIds = SecurityUtils.getCurrentUserDivisionIds();
            queryParams.and("division.id", Filter.Operator.in, userDivisionIds);
        }

        // only active vehicle...
        queryParams.and("status", Filter.Operator.eq, RecordStatus.ACTIVE);

        if (StringUtils.isNotEmpty(vm.getBrand())) {
            queryParams.and("brand", Filter.Operator.like, vm.getBrand());
        }
        if (StringUtils.isNotEmpty(vm.getName())) {
            queryParams.and("name", Filter.Operator.like, vm.getName());
        }

        if (StringUtils.isNotEmpty(vm.getLicensePlateNumber())) {
            queryParams.and("licensePlateNumber", Filter.Operator.like, vm.getLicensePlateNumber());
        }
        if (vm.getIsBounded() != null) {
            queryParams.and("bounded", Filter.Operator.eq, vm.getIsBounded());
        }

        if (vm.getIsOnline()!= null){
            queryParams.and("equipment.isOnline", Filter.Operator.eq, vm.getIsOnline());
        }

        if (vm instanceof VehicleBindingVM) {
            VehicleBindingVM bindingVM = (VehicleBindingVM) vm;
            if (StringUtils.isNotEmpty(bindingVM.getIdentifyNumber())) {
                queryParams.and("equipment.identifyNumber", Filter.Operator.like, bindingVM.getIdentifyNumber());
            }
        }


        return vehicleRepository.findAll(queryParams, pageable)
            .map(VehicleService::toDto);
    }

    /**
     * Get one vehicle by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleDetailsDTO> findOne(Long id) {
        log.debug("Request to get Vehicle : {}", id);
        return vehicleRepository.findById(id)
            .map(VehicleService::toDto);
    }

    @Override
    public void binding(EquipmentVehicleBindingVM vm) {
        log.debug("Request to binding equipment and vehicle: {}", vm);
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vm.getVehicleId()).filter(vehicle -> RecordStatus.ACTIVE.equals(vehicle.getStatus()));

        Optional<Equipment> equipmentOptional = equipmentRepository.findById(vm.getEquipmentId()).filter(equipment -> EquipmentStatus.DELETED.equals(equipment.getStatus()));


        if (!vehicleOptional.isPresent()) {
            throw new BadRequestAlertException("Invalid vehicle id", "Vehicle", "vehicle.notexist");
        }
        if (!equipmentOptional.isPresent()) {
            throw new BadRequestAlertException("Invalid equipment id", "Equipment", "equipment.notexist");
        }
        Vehicle vehicle = vehicleOptional.get();
        Equipment equipment = equipmentOptional.get();
        if (!EquipmentStatus.UNBOUND.equals(equipment.getStatus())) {
            throw new BadRequestAlertException("Equipment is bound already", "Binding", "equipment.binding.boundalready");
        }
        if (vehicle.getBounded()) {
            throw new BadRequestAlertException("Vehicle is bound already", "Binding", "vehicle.binding.boundalready");
        }
        equipment.setVehicle(vehicle);
        equipment.setStatus(EquipmentStatus.BOUND);
        vehicle.setBounded(true);
    }

    /**
     * Delete the vehicle by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Vehicle : {}", id);
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(id);
        if (vehicleOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();
            SecurityUtils.checkDataPermission(vehicle.getDivision());
            vehicle.setStatus(RecordStatus.DELETED);
            vehicle.setBounded(false);
            if (vehicle.getEquipment() != null) {
                vehicle.getEquipment().setStatus(EquipmentStatus.UNBOUND);
                vehicle.getEquipment().setVehicle(null);
            }
        }
        // vehicleRepository.deleteById(id);
    }

    @Override
    public List<VehicleSummaryDTO> findVehicleByDivisionId(Long divisionId) {
        List<VehicleSummaryDTO> vehicles = vehicleRepository.findByDivisionIdAndStatus(divisionId, RecordStatus.ACTIVE)
            .stream().map(VehicleService::toSummaryDto).collect(Collectors.toList());
        return vehicles;
    }

    @Override
    public List<UploadResultDTO> binding(List<BindingData> data) {

        List<UploadResultDTO> results = new ArrayList<>();

        for (BindingData bindingData : data) {
            boolean hasError = false;
            Optional<Vehicle> vehicleOptional = vehicleRepository.findOneByLicensePlateNumberAndStatus(bindingData.getLicensePlateNumber(), RecordStatus.ACTIVE);
            Optional<Equipment> equipmentOptional = equipmentRepository.findOneByIdentifyNumberAndStatusNot(bindingData.getIdentifyNumber(), EquipmentStatus.DELETED);

            if (!vehicleOptional.isPresent()) {
                createResult("binding.upload.vehicle.not.exist", bindingData.getRowIdx(), false, results);
                hasError = true;
            }
            if (!equipmentOptional.isPresent()) {
                createResult("binding.upload.equipment.not.exist", bindingData.getRowIdx(), false, results);
                hasError = true;

            }
            if (vehicleOptional.isPresent() && equipmentOptional.isPresent()) {
                Vehicle vehicle = vehicleOptional.get();
                Equipment equipment = equipmentOptional.get();
                if (!EquipmentStatus.UNBOUND.equals(equipment.getStatus())) {
                    createResult("binding.upload.equipment.bound.already", bindingData.getRowIdx(), false, results);
                    hasError = true;
                }
                if (vehicle.getBounded()) {
                    createResult("binding.upload.vehicle.bound.already", bindingData.getRowIdx(), false, results);
                    hasError = true;
                }
            }
            if (!hasError) {
                Vehicle vehicle = vehicleOptional.get();
                Equipment equipment = equipmentOptional.get();
                equipment.setVehicle(vehicle);
                equipment.setStatus(EquipmentStatus.BOUND);
                vehicle.setBounded(true);
            }
        }
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
