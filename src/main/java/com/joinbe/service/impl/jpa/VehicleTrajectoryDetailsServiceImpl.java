package com.joinbe.service.impl.jpa;

import com.joinbe.domain.VehicleTrajectoryDetails;
import com.joinbe.repository.VehicleTrajectoryDetailsRepository;
import com.joinbe.service.VehicleTrajectoryDetailsService;
import com.joinbe.service.dto.VehicleTrajectoryDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link VehicleTrajectoryDetails}.
 */
@Service("JpaVehicleTrajectoryDetailsService")
@Transactional
public class VehicleTrajectoryDetailsServiceImpl implements VehicleTrajectoryDetailsService {

    private final Logger log = LoggerFactory.getLogger(VehicleTrajectoryDetailsServiceImpl.class);

    private final VehicleTrajectoryDetailsRepository vehicleTrajectoryDetailsRepository;


    public VehicleTrajectoryDetailsServiceImpl(VehicleTrajectoryDetailsRepository vehicleTrajectoryDetailsRepository) {
        this.vehicleTrajectoryDetailsRepository = vehicleTrajectoryDetailsRepository;
    }

    /**
     * Save a vehicleTrajectoryDetails.
     *
     * @param vehicleTrajectoryDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VehicleTrajectoryDetailsDTO save(VehicleTrajectoryDetailsDTO vehicleTrajectoryDetailsDTO) {
        log.debug("Request to save VehicleTrajectoryDetails : {}", vehicleTrajectoryDetailsDTO);
        VehicleTrajectoryDetails vehicleTrajectoryDetails = VehicleTrajectoryDetailsService.toEntity(vehicleTrajectoryDetailsDTO);
        vehicleTrajectoryDetails = vehicleTrajectoryDetailsRepository.save(vehicleTrajectoryDetails);
        return VehicleTrajectoryDetailsService.toDto(vehicleTrajectoryDetails);
    }

    /**
     * Get all the vehicleTrajectoryDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VehicleTrajectoryDetailsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all VehicleTrajectoryDetails");
        return vehicleTrajectoryDetailsRepository.findAll(pageable)
            .map(VehicleTrajectoryDetailsService::toDto);
    }

    /**
     * Get one vehicleTrajectoryDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleTrajectoryDetailsDTO> findOne(Long id) {
        log.debug("Request to get VehicleTrajectoryDetails : {}", id);
        return vehicleTrajectoryDetailsRepository.findById(id)
            .map(VehicleTrajectoryDetailsService::toDto);
    }

    /**
     * Delete the vehicleTrajectoryDetails by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete VehicleTrajectoryDetails : {}", id);
        vehicleTrajectoryDetailsRepository.deleteById(id);
    }
}
