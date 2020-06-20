package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Division;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.Vehicle;
import com.joinbe.repository.DivisionRepository;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.VehicleService;
import com.joinbe.service.dto.DivisionDTO;
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

import java.util.List;
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

    private final DivisionRepository divisionRepository;


    public VehicleServiceImpl(VehicleRepository vehicleRepository, EquipmentRepository equipmentRepository,
                              DivisionRepository divisionRepository) {
        this.vehicleRepository = vehicleRepository;
        this.equipmentRepository = equipmentRepository;
        this.divisionRepository = divisionRepository;
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
        Optional<Division> division = divisionRepository.findById(vehicleDTO.getDivisionId());
        if(division.isPresent()){
            SecurityUtils.checkDataPermission(division.get());
            vehicle.setDivision(division.get());
        }else {
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
    public Page<VehicleDTO> findAll(Pageable pageable, VehicleVM vm) {
        log.debug("Request to get all Vehicles");
        QueryParams<Vehicle> queryParams = new QueryParams<>();
        if(vm.getDivisionId() != null) {
            SecurityUtils.checkDataPermission(vm.getDivisionId());
            queryParams.and("division.id", Filter.Operator.eq, vm.getDivisionId());
        }else {
            // add user's division condition
            List<Long> userDivisionIds = SecurityUtils.getCurrentUserDivisionIds();
            queryParams.and("division.id", Filter.Operator.in, userDivisionIds);
        }


        queryParams.and("status", Filter.Operator.eq, "A");
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
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(id);
        if(vehicleOptional.isPresent()){
            Vehicle vehicle = vehicleOptional.get();
            SecurityUtils.checkDataPermission(vehicle.getDivision());
            vehicle.setStatus("D");
        }
       // vehicleRepository.deleteById(id);
    }

}
