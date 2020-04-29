package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.VehicleTrajectory;
import com.joinbe.service.dto.VehicleTrajectoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VehicleTrajectoryDTO> findAll(Pageable pageable);

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
}
