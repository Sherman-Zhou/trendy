package com.joinbe.web.rest;

import com.joinbe.service.VehicleMaintenanceService;
import com.joinbe.service.dto.VehicleMaintenanceDTO;
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
 * REST controller for managing {@link com.joinbe.domain.VehicleMaintenance}.
 */
@RestController
@RequestMapping("/api")
public class VehicleMaintenanceResource {

    private static final String ENTITY_NAME = "vehicleMaintenance";
    private final Logger log = LoggerFactory.getLogger(VehicleMaintenanceResource.class);
    private final VehicleMaintenanceService vehicleMaintenanceService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public VehicleMaintenanceResource(VehicleMaintenanceService vehicleMaintenanceService) {
        this.vehicleMaintenanceService = vehicleMaintenanceService;
    }

    /**
     * {@code POST  /vehicle-maintenances} : Create a new vehicleMaintenance.
     *
     * @param vehicleMaintenanceDTO the vehicleMaintenanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vehicleMaintenanceDTO, or with status {@code 400 (Bad Request)} if the vehicleMaintenance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vehicle-maintenances")
    public ResponseEntity<VehicleMaintenanceDTO> createVehicleMaintenance(@Valid @RequestBody VehicleMaintenanceDTO vehicleMaintenanceDTO) throws URISyntaxException {
        log.debug("REST request to save VehicleMaintenance : {}", vehicleMaintenanceDTO);
        if (vehicleMaintenanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new vehicleMaintenance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VehicleMaintenanceDTO result = vehicleMaintenanceService.save(vehicleMaintenanceDTO);
        return ResponseEntity.created(new URI("/api/vehicle-maintenances/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /vehicle-maintenances} : Updates an existing vehicleMaintenance.
     *
     * @param vehicleMaintenanceDTO the vehicleMaintenanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehicleMaintenanceDTO,
     * or with status {@code 400 (Bad Request)} if the vehicleMaintenanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vehicleMaintenanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/vehicle-maintenances")
    public ResponseEntity<VehicleMaintenanceDTO> updateVehicleMaintenance(@Valid @RequestBody VehicleMaintenanceDTO vehicleMaintenanceDTO) throws URISyntaxException {
        log.debug("REST request to update VehicleMaintenance : {}", vehicleMaintenanceDTO);
        if (vehicleMaintenanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        VehicleMaintenanceDTO result = vehicleMaintenanceService.save(vehicleMaintenanceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vehicleMaintenanceDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /vehicle-maintenances} : get all the vehicleMaintenances.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicleMaintenances in body.
     */
    @GetMapping("/vehicle-maintenances")
    public ResponseEntity<List<VehicleMaintenanceDTO>> getAllVehicleMaintenances(Pageable pageable) {
        log.debug("REST request to get a page of VehicleMaintenances");
        Page<VehicleMaintenanceDTO> page = vehicleMaintenanceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vehicle-maintenances/:id} : get the "id" vehicleMaintenance.
     *
     * @param id the id of the vehicleMaintenanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicleMaintenanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vehicle-maintenances/{id}")
    public ResponseEntity<VehicleMaintenanceDTO> getVehicleMaintenance(@PathVariable Long id) {
        log.debug("REST request to get VehicleMaintenance : {}", id);
        Optional<VehicleMaintenanceDTO> vehicleMaintenanceDTO = vehicleMaintenanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vehicleMaintenanceDTO);
    }

    /**
     * {@code DELETE  /vehicle-maintenances/:id} : delete the "id" vehicleMaintenance.
     *
     * @param id the id of the vehicleMaintenanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/vehicle-maintenances/{id}")
    public ResponseEntity<Void> deleteVehicleMaintenance(@PathVariable Long id) {
        log.debug("REST request to delete VehicleMaintenance : {}", id);
        vehicleMaintenanceService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
