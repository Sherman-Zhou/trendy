package com.joinbe.web.rest;

import com.joinbe.service.VehicleTrajectoryService;
import com.joinbe.service.dto.VehicleTrajectoryDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.joinbe.domain.VehicleTrajectory}.
 */
//@RestController
@RequestMapping("/api")
public class VehicleMonitoringResource {

    private static final String ENTITY_NAME = "vehicleTrajectory";
    private final Logger log = LoggerFactory.getLogger(VehicleMonitoringResource.class);
    private final VehicleTrajectoryService vehicleTrajectoryService;


    public VehicleMonitoringResource(VehicleTrajectoryService vehicleTrajectoryService) {
        this.vehicleTrajectoryService = vehicleTrajectoryService;
    }

//    /**
//     * {@code POST  /vehicle-trajectories} : Create a new vehicleTrajectory.
//     *
//     * @param vehicleTrajectoryDTO the vehicleTrajectoryDTO to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vehicleTrajectoryDTO, or with status {@code 400 (Bad Request)} if the vehicleTrajectory has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/vehicle-trajectories")
//    public ResponseEntity<VehicleTrajectoryDTO> createVehicleTrajectory(@Valid @RequestBody VehicleTrajectoryDTO vehicleTrajectoryDTO) throws URISyntaxException {
//        log.debug("REST request to save VehicleTrajectory : {}", vehicleTrajectoryDTO);
//        if (vehicleTrajectoryDTO.getId() != null) {
//            throw new BadRequestAlertException("A new vehicleTrajectory cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        VehicleTrajectoryDTO result = vehicleTrajectoryService.save(vehicleTrajectoryDTO);
//        return ResponseEntity.created(new URI("/api/vehicle-trajectories/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /vehicle-trajectories} : Updates an existing vehicleTrajectory.
//     *
//     * @param vehicleTrajectoryDTO the vehicleTrajectoryDTO to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehicleTrajectoryDTO,
//     * or with status {@code 400 (Bad Request)} if the vehicleTrajectoryDTO is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the vehicleTrajectoryDTO couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/vehicle-trajectories")
//    public ResponseEntity<VehicleTrajectoryDTO> updateVehicleTrajectory(@Valid @RequestBody VehicleTrajectoryDTO vehicleTrajectoryDTO) throws URISyntaxException {
//        log.debug("REST request to update VehicleTrajectory : {}", vehicleTrajectoryDTO);
//        if (vehicleTrajectoryDTO.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        VehicleTrajectoryDTO result = vehicleTrajectoryService.save(vehicleTrajectoryDTO);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vehicleTrajectoryDTO.getId().toString()))
//            .body(result);
//    }

    /**
     * {@code GET  /vehicle-trajectories} : get all the vehicleTrajectories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicleTrajectories in body.
     */
    @GetMapping("/vehicle-trajectories")
    public ResponseEntity<List<VehicleTrajectoryDTO>> getAllVehicleTrajectories(Pageable pageable) {
        log.debug("REST request to get a page of VehicleTrajectories");
        Page<VehicleTrajectoryDTO> page = vehicleTrajectoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vehicle-trajectories/:id} : get the "id" vehicleTrajectory.
     *
     * @param id the id of the vehicleTrajectoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicleTrajectoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vehicle-trajectories/{id}")
    public ResponseEntity<VehicleTrajectoryDTO> getVehicleTrajectory(@PathVariable Long id) {
        log.debug("REST request to get VehicleTrajectory : {}", id);
        Optional<VehicleTrajectoryDTO> vehicleTrajectoryDTO = vehicleTrajectoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vehicleTrajectoryDTO);
    }

//    /**
//     * {@code DELETE  /vehicle-trajectories/:id} : delete the "id" vehicleTrajectory.
//     *
//     * @param id the id of the vehicleTrajectoryDTO to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/vehicle-trajectories/{id}")
//    public ResponseEntity<Void> deleteVehicleTrajectory(@PathVariable Long id) {
//        log.debug("REST request to delete VehicleTrajectory : {}", id);
//        vehicleTrajectoryService.delete(id);
//        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
//    }
}
