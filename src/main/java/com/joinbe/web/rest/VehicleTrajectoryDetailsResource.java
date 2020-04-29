package com.joinbe.web.rest;

import com.joinbe.service.VehicleTrajectoryDetailsService;
import com.joinbe.service.dto.VehicleTrajectoryDetailsDTO;
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
 * REST controller for managing {@link com.joinbe.domain.VehicleTrajectoryDetails}.
 */
@RestController
@RequestMapping("/api")
public class VehicleTrajectoryDetailsResource {

    private static final String ENTITY_NAME = "vehicleTrajectoryDetails";
    private final Logger log = LoggerFactory.getLogger(VehicleTrajectoryDetailsResource.class);
    private final VehicleTrajectoryDetailsService vehicleTrajectoryDetailsService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public VehicleTrajectoryDetailsResource(VehicleTrajectoryDetailsService vehicleTrajectoryDetailsService) {
        this.vehicleTrajectoryDetailsService = vehicleTrajectoryDetailsService;
    }

    /**
     * {@code POST  /vehicle-trajectory-details} : Create a new vehicleTrajectoryDetails.
     *
     * @param vehicleTrajectoryDetailsDTO the vehicleTrajectoryDetailsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vehicleTrajectoryDetailsDTO, or with status {@code 400 (Bad Request)} if the vehicleTrajectoryDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vehicle-trajectory-details")
    public ResponseEntity<VehicleTrajectoryDetailsDTO> createVehicleTrajectoryDetails(@Valid @RequestBody VehicleTrajectoryDetailsDTO vehicleTrajectoryDetailsDTO) throws URISyntaxException {
        log.debug("REST request to save VehicleTrajectoryDetails : {}", vehicleTrajectoryDetailsDTO);
        if (vehicleTrajectoryDetailsDTO.getId() != null) {
            throw new BadRequestAlertException("A new vehicleTrajectoryDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VehicleTrajectoryDetailsDTO result = vehicleTrajectoryDetailsService.save(vehicleTrajectoryDetailsDTO);
        return ResponseEntity.created(new URI("/api/vehicle-trajectory-details/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /vehicle-trajectory-details} : Updates an existing vehicleTrajectoryDetails.
     *
     * @param vehicleTrajectoryDetailsDTO the vehicleTrajectoryDetailsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehicleTrajectoryDetailsDTO,
     * or with status {@code 400 (Bad Request)} if the vehicleTrajectoryDetailsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vehicleTrajectoryDetailsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/vehicle-trajectory-details")
    public ResponseEntity<VehicleTrajectoryDetailsDTO> updateVehicleTrajectoryDetails(@Valid @RequestBody VehicleTrajectoryDetailsDTO vehicleTrajectoryDetailsDTO) throws URISyntaxException {
        log.debug("REST request to update VehicleTrajectoryDetails : {}", vehicleTrajectoryDetailsDTO);
        if (vehicleTrajectoryDetailsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        VehicleTrajectoryDetailsDTO result = vehicleTrajectoryDetailsService.save(vehicleTrajectoryDetailsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vehicleTrajectoryDetailsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /vehicle-trajectory-details} : get all the vehicleTrajectoryDetails.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicleTrajectoryDetails in body.
     */
    @GetMapping("/vehicle-trajectory-details")
    public ResponseEntity<List<VehicleTrajectoryDetailsDTO>> getAllVehicleTrajectoryDetails(Pageable pageable) {
        log.debug("REST request to get a page of VehicleTrajectoryDetails");
        Page<VehicleTrajectoryDetailsDTO> page = vehicleTrajectoryDetailsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vehicle-trajectory-details/:id} : get the "id" vehicleTrajectoryDetails.
     *
     * @param id the id of the vehicleTrajectoryDetailsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicleTrajectoryDetailsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vehicle-trajectory-details/{id}")
    public ResponseEntity<VehicleTrajectoryDetailsDTO> getVehicleTrajectoryDetails(@PathVariable Long id) {
        log.debug("REST request to get VehicleTrajectoryDetails : {}", id);
        Optional<VehicleTrajectoryDetailsDTO> vehicleTrajectoryDetailsDTO = vehicleTrajectoryDetailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vehicleTrajectoryDetailsDTO);
    }

    /**
     * {@code DELETE  /vehicle-trajectory-details/:id} : delete the "id" vehicleTrajectoryDetails.
     *
     * @param id the id of the vehicleTrajectoryDetailsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/vehicle-trajectory-details/{id}")
    public ResponseEntity<Void> deleteVehicleTrajectoryDetails(@PathVariable Long id) {
        log.debug("REST request to delete VehicleTrajectoryDetails : {}", id);
        vehicleTrajectoryDetailsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
