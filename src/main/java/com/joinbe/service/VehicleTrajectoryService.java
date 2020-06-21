package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.VehicleTrajectory;
import com.joinbe.service.dto.DivisionWithVehicesleDTO;
import com.joinbe.service.dto.VehicleStateDTO;
import com.joinbe.service.dto.VehicleTrajectoryDTO;
import com.joinbe.service.dto.VehicleTrajectoryDetailsDTO;
import com.joinbe.web.rest.vm.SearchVehicleVM;
import com.joinbe.web.rest.vm.TrajectoryVM;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.VehicleTrajectory}.
 */
public interface VehicleTrajectoryService {

    static VehicleTrajectoryDTO toDto(VehicleTrajectory vehicleTrajectory) {

        return BeanConverter.toDto(vehicleTrajectory, VehicleTrajectoryDTO.class);
    }

    static VehicleTrajectory toEntity(VehicleTrajectoryDTO vehicleTrajectoryDTO) {

        return BeanConverter.toEntity(vehicleTrajectoryDTO, VehicleTrajectory.class);
    }

    /**
     * Save a vehicleTrajectory.
     *
     * @param vehicleTrajectoryDTO the entity to save.
     * @return the persisted entity.
     */
    VehicleTrajectoryDTO save(VehicleTrajectoryDTO vehicleTrajectoryDTO);

    /**
     * Get all the vehicleTrajectories.
     *
     * @return the list of entities.
     */
    List<VehicleTrajectoryDTO> findAll(TrajectoryVM vm);


    /**
     * Get all the vehicleTrajectories.
     *
     * @return the list of entities.
     */
    List<VehicleTrajectoryDetailsDTO> findAllDetails(TrajectoryVM vm);

    /**
     * Get the "id" vehicleTrajectory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VehicleTrajectoryDTO> findOne(Long id);

    /**
     * Delete the "id" vehicleTrajectory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<DivisionWithVehicesleDTO> findCurrentUserDivisionsAndVehicles(SearchVehicleVM vm);

    Optional<VehicleStateDTO> findVehicleCurrentState(Long vehicleId);

    List<String> findAllTrajectoryIds(Long vehicleId);
}
