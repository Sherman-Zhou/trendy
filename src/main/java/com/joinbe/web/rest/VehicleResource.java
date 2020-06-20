package com.joinbe.web.rest;

import com.joinbe.domain.Division;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.DivisionService;
import com.joinbe.service.VehicleService;
import com.joinbe.service.dto.DivisionDTO;
import com.joinbe.service.dto.VehicleDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import com.joinbe.web.rest.vm.VehicleVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * REST controller for managing {@link com.joinbe.domain.Vehicle}.
 */
@RestController
@RequestMapping("/api")
@Api(value = "车辆管理相关接口", tags = {"车辆管理相关接口"})
public class VehicleResource {

    private static final String ENTITY_NAME = "vehicle";
    private final Logger log = LoggerFactory.getLogger(VehicleResource.class);
    private final VehicleService vehicleService;
    private final DivisionService divisionService;


    public VehicleResource(VehicleService vehicleService, DivisionService divisionService) {
        this.vehicleService = vehicleService;
        this.divisionService = divisionService;
    }


    /**
     * {@code GET  /vehicles} : get all the vehicles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicles in body.
     */
    @GetMapping("/vehicles")
    @ApiOperation("搜索车辆")
    public ResponseEntity<PageData<VehicleDTO>> getAllVehicles(Pageable pageable, VehicleVM vm) {
        log.debug("REST request to get a page of Vehicles");
        Page<VehicleDTO> page = vehicleService.findAll(pageable, vm);
        return ResponseUtil.toPageData(page);
    }

    /**
     * {@code GET  /vehicles/:id} : get the "id" vehicle.
     *
     * @param id the id of the vehicleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vehicles/{id}")
    @ApiOperation("获取车辆详情")
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable @ApiParam(value = "车辆主键", required = true) Long id) {
        log.debug("REST request to get Vehicle : {}", id);
        Optional<VehicleDTO> vehicleDTO = vehicleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vehicleDTO);
    }

    /**
     * {@code GET  /vehicles} : sync vehicle from App backend.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicleDTO}.
     */
    @GetMapping("/vehicles/sync")
    @ApiOperation("同步车辆")
    public ResponseEntity<PageData<VehicleDTO>> syncVehicle(Pageable pageable) {
        log.debug("sync cars from app backend..."); //TODO: to implement...
        Page<VehicleDTO> page = vehicleService.findAll(pageable, new VehicleVM());
        return ResponseUtil.toPageData(page);
    }


    /**
     * {@code POST  /vehicles} : Create a new vehicle.
     *
     * @param vehicleDTO the vehicleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vehicleDTO, or with status {@code 400 (Bad Request)} if the vehicle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vehicles")
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) throws URISyntaxException {
        log.debug("REST request to save Vehicle : {}", vehicleDTO);
        if (vehicleDTO.getId() != null) {
            throw new BadRequestAlertException("A new vehicle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VehicleDTO result = vehicleService.save(vehicleDTO);
        return ResponseEntity.created(new URI("/api/vehicles/" + result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /vehicles} : Updates an existing vehicle.
     *
     * @param vehicleDTO the vehicleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehicleDTO,
     * or with status {@code 400 (Bad Request)} if the vehicleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vehicleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/vehicles")
    public ResponseEntity<VehicleDTO> updateVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) throws URISyntaxException {
        log.debug("REST request to update Vehicle : {}", vehicleDTO);
        if (vehicleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        VehicleDTO result = vehicleService.save(vehicleDTO);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * {@code DELETE  /vehicles/:id} : delete the "id" vehicle.
     *
     * @param id the id of the vehicleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        log.debug("REST request to delete Vehicle : {}", id);
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
