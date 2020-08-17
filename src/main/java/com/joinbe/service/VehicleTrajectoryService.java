package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.VehicleTrajectory;
import com.joinbe.domain.enumeration.PaymentStatus;
import com.joinbe.security.UserLoginInfo;
import com.joinbe.service.dto.*;
import com.joinbe.web.rest.vm.SearchVehicleVM;
import com.joinbe.web.rest.vm.TrajectoryVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.VehicleTrajectory}.
 */
public interface VehicleTrajectoryService {

    static VehicleTrajectoryDTO toDto(VehicleTrajectory vehicleTrajectory) {
        VehicleTrajectoryDTO dto = BeanConverter.toDto(vehicleTrajectory, VehicleTrajectoryDTO.class);
        dto.setStatus(PaymentStatus.getCode(vehicleTrajectory.getStatus()));
        return dto;
    }

    static VehicleTrajectory toEntity(VehicleTrajectoryDTO vehicleTrajectoryDTO) {
        VehicleTrajectory entity = BeanConverter.toEntity(vehicleTrajectoryDTO, VehicleTrajectory.class);
        entity.setStatus(PaymentStatus.resolve(vehicleTrajectoryDTO.getStatus()));
        return entity;
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
    Page<VehicleTrajectoryDTO> findAll(Pageable pageable, TrajectoryVM vm);


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

    List<TrajectoryReportDTO> findAllTrajectory4backup(Instant from, Instant to);

    void backupTrajectory(Locale locale, UserLoginInfo loginInfo);
}
