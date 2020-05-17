package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.Vehicle;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.service.VehicleService;
import com.joinbe.service.dto.VehicleDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.EquipmentVehicleBindingVM;
import com.joinbe.web.rest.vm.VehicleBindingVM;
import com.joinbe.web.rest.vm.VehicleVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Vehicle}.
 */
@Service("JpaVehicleService")
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final Logger log = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository vehicleRepository;

    private final EquipmentRepository equipmentRepository;


    public VehicleServiceImpl(VehicleRepository vehicleRepository, EquipmentRepository equipmentRepository) {
        this.vehicleRepository = vehicleRepository;
        this.equipmentRepository = equipmentRepository;
    }

    /**
     * Save a vehicle.
     *
     * @param vehicleDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VehicleDTO save(VehicleDTO vehicleDTO) {
        log.debug("Request to save Vehicle : {}", vehicleDTO);
        Vehicle vehicle = VehicleService.toEntity(vehicleDTO);
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
    public Page<VehicleDTO> findAll(Pageable pageable, VehicleVM vm) {
        log.debug("Request to get all Vehicles");
        QueryParams<Vehicle> queryParams = new QueryParams<>();

        if (StringUtils.isNotEmpty(vm.getBrand())) {
            queryParams.and("brand", Filter.Operator.eq, vm.getBrand());
        }
        if (StringUtils.isNotEmpty(vm.getName())) {
            queryParams.and("name", Filter.Operator.eq, vm.getName());
        }
        if (vm.getIsBounded() != null) {
            queryParams.and("equipment", vm.getIsBounded() ? Filter.Operator.isNotNull : Filter.Operator.isNull, null);
        }

        if (vm.getIsOnline()) {
            queryParams.and("equipment.isOnline", Filter.Operator.eq, Boolean.TRUE);
        }

        if (vm instanceof VehicleBindingVM) {
            VehicleBindingVM bindingVM = (VehicleBindingVM) vm;
            if (StringUtils.isNotEmpty(bindingVM.getIdentifyNumber())) {
                queryParams.and("equipment.identifyNumber", Filter.Operator.eq, bindingVM.getIdentifyNumber());
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
    public Optional<VehicleDTO> findOne(Long id) {
        log.debug("Request to get Vehicle : {}", id);
        return vehicleRepository.findById(id)
            .map(VehicleService::toDto);
    }

    @Override
    public void binding(EquipmentVehicleBindingVM vm) {
        log.debug("Request to binding equipment and vehicle: {}", vm);
        Optional<Vehicle> vehicle = vehicleRepository.findById(vm.getVehicleId());
        Optional<Equipment> equipment = equipmentRepository.findById(vm.getEquipmentId());
        if (!vehicle.isPresent()) {
            throw new BadRequestAlertException("Invalid vehicle id", "Vehicle", "vehicle.notexist");
        }
        if (!equipment.isPresent()) {
            throw new BadRequestAlertException("Invalid equipment id", "Equipment", "equipment.notexist");
        }
        if (equipment.get().getVehicle() != null || vehicle.get().getEquipment() != null) {
            throw new BadRequestAlertException("Bound already", "Binding", "binding.boundalready");
        }
        equipment.get().setVehicle(vehicle.get());
    }

    /**
     * Delete the vehicle by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Vehicle : {}", id);
        vehicleRepository.deleteById(id);
    }
}
