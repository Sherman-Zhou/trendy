package com.joinbe.service.impl.jpa;

import com.joinbe.domain.VehicleTrajectory;
import com.joinbe.repository.VehicleTrajectoryRepository;
import com.joinbe.service.VehicleTrajectoryService;
import com.joinbe.service.dto.VehicleTrajectoryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link VehicleTrajectory}.
 */
@Service("JpaVehicleTrajectoryService")
@Transactional
public class VehicleTrajectoryServiceImpl implements VehicleTrajectoryService {

    private final Logger log = LoggerFactory.getLogger(VehicleTrajectoryServiceImpl.class);

    private final VehicleTrajectoryRepository vehicleTrajectoryRepository;


    public VehicleTrajectoryServiceImpl(VehicleTrajectoryRepository vehicleTrajectoryRepository) {
        this.vehicleTrajectoryRepository = vehicleTrajectoryRepository;
    }

    /**
     * Save a vehicleTrajectory.
     *
     * @param vehicleTrajectoryDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VehicleTrajectoryDTO save(VehicleTrajectoryDTO vehicleTrajectoryDTO) {
        log.debug("Request to save VehicleTrajectory : {}", vehicleTrajectoryDTO);
        VehicleTrajectory vehicleTrajectory = VehicleTrajectoryService.toEntity(vehicleTrajectoryDTO);
        vehicleTrajectory = vehicleTrajectoryRepository.save(vehicleTrajectory);
        return VehicleTrajectoryService.toDto(vehicleTrajectory);
    }

    /**
     * Get all the vehicleTrajectories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VehicleTrajectoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all VehicleTrajectories");
        return vehicleTrajectoryRepository.findAll(pageable)
            .map(VehicleTrajectoryService::toDto);
    }

    /**
     * Get one vehicleTrajectory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleTrajectoryDTO> findOne(Long id) {
        log.debug("Request to get VehicleTrajectory : {}", id);
        return vehicleTrajectoryRepository.findById(id)
            .map(VehicleTrajectoryService::toDto);
    }

    /**
     * Delete the vehicleTrajectory by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete VehicleTrajectory : {}", id);
        vehicleTrajectoryRepository.deleteById(id);
    }
}
