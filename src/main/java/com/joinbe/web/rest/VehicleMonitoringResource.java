package com.joinbe.web.rest;

import com.joinbe.service.VehicleService;
import com.joinbe.service.VehicleTrajectoryService;
import com.joinbe.service.dto.DivisionWithVehicesleDTO;
import com.joinbe.service.dto.VehicleStateDTO;
import com.joinbe.service.dto.VehicleTrajectoryDTO;
import com.joinbe.service.dto.VehicleTrajectoryDetailsDTO;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import com.joinbe.web.rest.vm.SearchVehicleVM;
import com.joinbe.web.rest.vm.TrajectoryVM;
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

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.joinbe.domain.VehicleTrajectory}.
 */
@RestController
@RequestMapping("/api")
@Api(value = "综合监控相关接口", tags = {"综合监控相关接口"})
public class VehicleMonitoringResource {

    private static final String ENTITY_NAME = "vehicleTrajectory";
    private final Logger log = LoggerFactory.getLogger(VehicleMonitoringResource.class);
    private final VehicleTrajectoryService vehicleTrajectoryService;

    private final VehicleService vehicleService;


    public VehicleMonitoringResource(VehicleTrajectoryService vehicleTrajectoryService, VehicleService vehicleService) {
        this.vehicleTrajectoryService = vehicleTrajectoryService;
        this.vehicleService = vehicleService;
    }


    @GetMapping("/monitor/vehicles/search")
    @ApiOperation(value = "获取部门车辆")
    public List<DivisionWithVehicesleDTO> searchVehicles(SearchVehicleVM vm) {
        log.debug("REST request to get VehicleTrajectory : {}", vm);
        return vehicleTrajectoryService.findCurrentUserDivisionsAndVehicles(vm);
    }

    /**
     * {@code GET  /vehicle-trajectories} : get all the vehicleTrajectories.     *
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicleTrajectories in body.
     */
    @GetMapping("/monitor/trajectories")
    @ApiOperation(value = "获取车辆轨迹")
    public ResponseEntity<PageData<VehicleTrajectoryDTO>> getAllVehicleTrajectories(Pageable pageable, TrajectoryVM vm) {
        log.debug("REST request to get a page of VehicleTrajectories");
        Page<VehicleTrajectoryDTO> dtos = vehicleTrajectoryService.findAll(pageable, vm);
        return ResponseUtil.toPageData(dtos);
    }


    /**
     * {@code GET  /monitor/trajectory-details} : get all the Trajectories details.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicleTrajectories in body.
     */
    @GetMapping("/monitor/trajectory-details")
    @ApiOperation(value = "获取车辆轨迹详情")
    public ResponseEntity<List<VehicleTrajectoryDetailsDTO>> getAllVehicleTrajectoryDetails(TrajectoryVM vm) {
        log.debug("REST request to get a List of getAllVehicleTrajectoryDetails");
        List<VehicleTrajectoryDetailsDTO> dtos = vehicleTrajectoryService.findAllDetails(vm);
        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/monitor/trajectoryIds/{vehicleId}")
    @ApiOperation("获取车辆轨迹Id")
    public List<String> findAllTrajectoryIds(@PathVariable @ApiParam(value = "车辆主键", required = true) Long vehicleId) {
        return vehicleTrajectoryService.findAllTrajectoryIds(vehicleId);
    }

    /**
     * {@code GET  /monitor/vehicle/current-state/:vehicleId} : get the current location of the vehicle :vehicleId.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the  location, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/monitor/vehicle/current-state/{vehicleId}")
    @ApiOperation("获取车辆实时状态")
    public ResponseEntity<VehicleStateDTO> getLatestVehicleTrajectory(@PathVariable @ApiParam(value = "车辆主键", required = true) Long vehicleId) {
        log.debug("REST request to get VehicleTrajectory : {}", vehicleId);
        Optional<VehicleStateDTO> vehicleStateDTO = vehicleTrajectoryService.findVehicleCurrentState(vehicleId);
        return ResponseUtil.wrapOrNotFound(vehicleStateDTO);
    }

}
