package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.VehicleMaintenance;
import com.joinbe.service.dto.VehicleMaintenanceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.VehicleMaintenance}.
 */
public interface VehicleMaintenanceService {

    static VehicleMaintenanceDTO toDto(VehicleMaintenance vehicleMaintenance) {

        return BeanConverter.toDto(vehicleMaintenance, VehicleMaintenanceDTO.class);
    }

    static VehicleMaintenance toEntity(VehicleMaintenanceDTO vehicleMaintenanceDTO) {

        return BeanConverter.toEntity(vehicleMaintenanceDTO, VehicleMaintenance.class);
    }

    /**
     * Save a vehicleMaintenance.
     *
     * @param vehicleMaintenanceDTO the entity to save.
     * @return the persisted entity.
     */
    VehicleMaintenanceDTO save(VehicleMaintenanceDTO vehicleMaintenanceDTO);

    /**
     * Get all the vehicleMaintenances.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VehicleMaintenanceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" vehicleMaintenance.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VehicleMaintenanceDTO> findOne(Long id);

    /**
     * Delete the "id" vehicleMaintenance.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
