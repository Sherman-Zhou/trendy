package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.Vehicle;
import com.joinbe.service.dto.VehicleDTO;
import com.joinbe.web.rest.vm.EquipmentVehicleBindingVM;
import com.joinbe.web.rest.vm.VehicleVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.Vehicle}.
 */
public interface VehicleService {

    static VehicleDTO toDto(Vehicle vehicle) {

        return BeanConverter.toDto(vehicle, VehicleDTO.class);
    }

    static Vehicle toEntity(VehicleDTO vehicleDTO) {

        return BeanConverter.toEntity(vehicleDTO, Vehicle.class);
    }

    /**
     * Save a vehicle.
     *
     * @param vehicleDTO the entity to save.
     * @return the persisted entity.
     */
    VehicleDTO save(VehicleDTO vehicleDTO);

    /**
     * Get all the vehicles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VehicleDTO> findAll(Pageable pageable, VehicleVM vm);

    /**
     * Get the "id" vehicle.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VehicleDTO> findOne(Long id);


    void binding(EquipmentVehicleBindingVM vm);

    /**
     * Delete the "id" vehicle.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

}
