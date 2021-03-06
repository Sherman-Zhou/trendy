package com.joinbe.web.rest;

import com.joinbe.data.collector.EquipmentController;
import com.joinbe.data.collector.service.dto.DeviceInitMileageReq;
import com.joinbe.service.VehicleMaintenanceService;
import com.joinbe.service.dto.VehicleMaintenanceDTO;
import com.joinbe.service.util.RestfulClient;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.ResponseUtil;
import com.joinbe.web.rest.vm.VehicleMaintenanceVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.*;

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
@Api(value = "车辆维护相关接口", tags = {"车辆维护相关接口"}, produces = "application/json")
public class VehicleMaintenanceResource {

    private static final String ENTITY_NAME = "vehicleMaintenance";
    private final Logger log = LoggerFactory.getLogger(VehicleMaintenanceResource.class);
    private final VehicleMaintenanceService vehicleMaintenanceService;

    private final RestfulClient restfulClient;

    private final EquipmentController equipmentController;


    public VehicleMaintenanceResource(VehicleMaintenanceService vehicleMaintenanceService, RestfulClient restfulClient, EquipmentController equipmentController) {
        this.vehicleMaintenanceService = vehicleMaintenanceService;
        this.restfulClient = restfulClient;
        this.equipmentController = equipmentController;
    }

    /**
     * {@code POST  /vehicle-maintenances} : Create a new vehicleMaintenance.
     *
     * @param vehicleMaintenanceDTO the vehicleMaintenanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vehicleMaintenanceDTO, or with status {@code 400 (Bad Request)} if the vehicleMaintenance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vehicle-maintenances")
    @ApiOperation("查询车辆维护记录")
    public ResponseEntity<VehicleMaintenanceDTO> createVehicleMaintenance(@Valid @RequestBody VehicleMaintenanceDTO vehicleMaintenanceDTO) throws URISyntaxException {
        log.debug("REST request to save VehicleMaintenance : {}", vehicleMaintenanceDTO);
        if (vehicleMaintenanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new vehicleMaintenance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VehicleMaintenanceDTO result = vehicleMaintenanceService.save(vehicleMaintenanceDTO);
        if (StringUtils.isNotEmpty(result.getImei())) {
            DeviceInitMileageReq req = new DeviceInitMileageReq();
            req.setImei(result.getImei());
            req.setInitMileage(result.getMileage());
            equipmentController.setMileageOffset(req, new BeanPropertyBindingResult(null, "DUMMY"));
        }
        return ResponseEntity.created(new URI("/api/vehicle-maintenances/" + result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /vehicle-maintenances} : Updates an existing vehicleMaintenance.
     *
     * @param vehicleMaintenanceDTO the vehicleMaintenanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehicleMaintenanceDTO,
     * or with status {@code 400 (Bad Request)} if the vehicleMaintenanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vehicleMaintenanceDTO couldn't be updated.
     */
    @PutMapping("/vehicle-maintenances")
    @ApiOperation("更新车辆维护记录")
    public ResponseEntity<VehicleMaintenanceDTO> updateVehicleMaintenance(@Valid @RequestBody VehicleMaintenanceDTO vehicleMaintenanceDTO) {
        log.debug("REST request to update VehicleMaintenance : {}", vehicleMaintenanceDTO);
        if (vehicleMaintenanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        VehicleMaintenanceDTO result = vehicleMaintenanceService.save(vehicleMaintenanceDTO);
        if (StringUtils.isNotEmpty(result.getImei())) {
            DeviceInitMileageReq req = new DeviceInitMileageReq();
            req.setImei(result.getImei());
            req.setInitMileage(result.getMileage());
            equipmentController.setMileageOffset(req, new BeanPropertyBindingResult(null, "DUMMY"));
        }
        return ResponseEntity.ok().body(result);
    }

//    /**
//     * {@code GET  /vehicle-maintenances} : get all the vehicleMaintenances.
//     *
//     * @param pageable the pagination information.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicleMaintenances in body.
//     */
//    @GetMapping("/vehicle-maintenances")
//    public ResponseEntity<PageData<VehicleMaintenanceDTO>> getAllVehicleMaintenances(Pageable pageable) {
//        log.debug("REST request to get a page of VehicleMaintenances");
//        Page<VehicleMaintenanceDTO> page = vehicleMaintenanceService.findAll(pageable);
//        return ResponseUtil.toPageData(page);
//    }

    /**
     * {@code GET  /vehicle-maintenances} : get all the vehicleMaintenances.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicleMaintenances in body.
     */
    @GetMapping("/vehicle-maintenances")
    @ApiOperation("获取所有车辆维护记录")
    public ResponseEntity<List<VehicleMaintenanceDTO>> getAllVehicleMaintenances(VehicleMaintenanceVM vm) {
        log.debug("REST request to get a page of VehicleMaintenances");
        List<VehicleMaintenanceDTO> vehicleMaintenances = vehicleMaintenanceService.findAll(vm);
        return ResponseEntity.ok().body(vehicleMaintenances);
    }

    /**
     * {@code GET  /vehicle-maintenances/:id} : get the "id" vehicleMaintenance.
     *
     * @param id the id of the vehicleMaintenanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicleMaintenanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vehicle-maintenances/{id}")
    @ApiOperation("获取车辆维护记录详情")
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
    @ApiOperation("删除车辆维护记录")
    public ResponseEntity<Void> deleteVehicleMaintenance(@PathVariable Long id) {
        log.debug("REST request to delete VehicleMaintenance : {}", id);
        vehicleMaintenanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
