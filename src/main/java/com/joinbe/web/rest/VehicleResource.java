package com.joinbe.web.rest;

import com.joinbe.service.VehicleService;
import com.joinbe.service.dto.VehicleDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    public VehicleResource(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
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
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable @ApiParam(value = "角色主键", required = true) Long id) {
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



}
