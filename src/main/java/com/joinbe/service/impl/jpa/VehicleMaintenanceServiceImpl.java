package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Vehicle;
import com.joinbe.domain.VehicleMaintenance;
import com.joinbe.repository.VehicleMaintenanceRepository;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.service.VehicleMaintenanceService;
import com.joinbe.service.dto.VehicleMaintenanceDTO;
import com.joinbe.web.rest.vm.VehicleMaintenanceVM;
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
 * Service Implementation for managing {@link VehicleMaintenance}.
 */
@Service("JpaVehicleMaintenanceService")
@Transactional
public class VehicleMaintenanceServiceImpl implements VehicleMaintenanceService {

    private final Logger log = LoggerFactory.getLogger(VehicleMaintenanceServiceImpl.class);

    private final VehicleMaintenanceRepository vehicleMaintenanceRepository;

    private final VehicleRepository vehicleRepository;

    public VehicleMaintenanceServiceImpl(VehicleMaintenanceRepository vehicleMaintenanceRepository, VehicleRepository vehicleRepository) {
        this.vehicleMaintenanceRepository = vehicleMaintenanceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Save a vehicleMaintenance.
     *
     * @param vehicleMaintenanceDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VehicleMaintenanceDTO save(VehicleMaintenanceDTO vehicleMaintenanceDTO) {
        log.debug("Request to save VehicleMaintenance : {}", vehicleMaintenanceDTO);
        VehicleMaintenance vehicleMaintenance = VehicleMaintenanceService.toEntity(vehicleMaintenanceDTO);
        Vehicle vehicle = vehicleRepository.getOne(vehicleMaintenanceDTO.getVehicleId());
        vehicleMaintenance.setVehicle(vehicle);
        vehicleMaintenance = vehicleMaintenanceRepository.save(vehicleMaintenance);
        return VehicleMaintenanceService.toDto(vehicleMaintenance);
    }

    /**
     * Get all the vehicleMaintenances.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VehicleMaintenanceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all VehicleMaintenances");
        return vehicleMaintenanceRepository.findAll(pageable)
            .map(VehicleMaintenanceService::toDto);
    }

    /**
     * Get one vehicleMaintenance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleMaintenanceDTO> findOne(Long id) {
        log.debug("Request to get VehicleMaintenance : {}", id);
        return vehicleMaintenanceRepository.findById(id)
            .map(VehicleMaintenanceService::toDto);
    }

    /**
     * Delete the vehicleMaintenance by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete VehicleMaintenance : {}", id);
        vehicleMaintenanceRepository.deleteById(id);
    }

    @Override
    public List<VehicleMaintenanceDTO> findAll(VehicleMaintenanceVM vm) {
        QueryParams<VehicleMaintenance> queryParams = new QueryParams<>();
        queryParams.and("vehicle.id", Filter.Operator.eq, vm.getVehicleId());
        return vehicleMaintenanceRepository.findAll(queryParams)
            .stream().map(VehicleMaintenanceService::toDto).collect(Collectors.toList());
    }
}
