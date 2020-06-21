package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.VehicleTrajectoryDetails;
import com.joinbe.service.dto.VehicleTrajectoryDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.VehicleTrajectoryDetails}.
 */
public interface VehicleTrajectoryDetailsService {

    static VehicleTrajectoryDetailsDTO toDto(VehicleTrajectoryDetails vehicleTrajectoryDetails) {
        VehicleTrajectoryDetailsDTO dto = BeanConverter.toDto(vehicleTrajectoryDetails, VehicleTrajectoryDetailsDTO.class);
        return dto;
    }

    static VehicleTrajectoryDetails toEntity(VehicleTrajectoryDetailsDTO vehicleTrajectoryDetailsDTO) {

        return BeanConverter.toEntity(vehicleTrajectoryDetailsDTO, VehicleTrajectoryDetails.class);
    }

    /**
     * Save a vehicleTrajectoryDetails.
     *
     * @param vehicleTrajectoryDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    VehicleTrajectoryDetailsDTO save(VehicleTrajectoryDetailsDTO vehicleTrajectoryDetailsDTO);

    /**
     * Get all the vehicleTrajectoryDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VehicleTrajectoryDetailsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" vehicleTrajectoryDetails.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VehicleTrajectoryDetailsDTO> findOne(Long id);

    /**
     * Delete the "id" vehicleTrajectoryDetails.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
