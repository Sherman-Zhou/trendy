package com.joinbe.web.rest;

import com.joinbe.domain.Vehicle;
import com.joinbe.service.VehicleService;
import com.joinbe.service.dto.VehicleDTO;
import com.joinbe.web.rest.vm.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;

/**
 * REST controller for managing {@link Vehicle}.
 */
@RestController
@RequestMapping("/api")
@Api(value = "车辆设备绑定相关接口", tags = {"车辆设备绑定相关接口"})
public class BindingResource {

    private final Logger log = LoggerFactory.getLogger(BindingResource.class);

    private final VehicleService vehicleService;


    public BindingResource(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }


    /**
     * {@code GET  /binding/search} : get all the vehicles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicles in body.
     */
    @GetMapping("/binding/search")
    @ApiOperation("搜索车辆")
    public ResponseEntity<PageData<VehicleDTO>> getAllVehicles(Pageable pageable, VehicleBindingVM vm) {
        log.debug("REST request to get a page of Vehicles");
        Page<VehicleDTO> page = vehicleService.findAll(pageable, vm);
        return ResponseUtil.toPageData(page);
    }

    /**
     * {@code Post  /vehicles/:id} : get the "id" vehicle.
     *
     * @param vm the id of the vehicle and equipment for binding.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicleDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("/binding")
    @ApiOperation("绑定设备")
    public ResponseEntity<Void> getVehicle(@RequestBody EquipmentVehicleBindingVM vm) {
        log.debug("REST request to bind Vehicle  {} and Equipment: {}", vm.getVehicleId(), vm.getEquipmentId());
        vehicleService.binding(vm);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET  /binding/:equipmentId/conn-testing} : sync vehicle from App backend.
     *
     * @param equipmentId the id of the Equipment to test.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the message}.
     */
    @GetMapping("/binding/{equipmentId}/conn-testing")
    @ApiOperation("连通测试")
    public ResponseEntity<String> connTesting(Long equipmentId) {
        log.debug("Connection testing with equipment: {}", equipmentId); //TODO: to implement...
        return ResponseEntity.ok().body("Successful");
    }

    @PostMapping("/binding/upload")
    @ApiOperation("导入绑定信息")
    public ResponseEntity<PageData<VehicleDTO>> upload(@RequestParam("file") MultipartFile file, Pageable pageable) {
        log.debug("uploaded file: {}", file.getOriginalFilename());
        Page<VehicleDTO> page = vehicleService.findAll(pageable, new VehicleVM());
        return ResponseUtil.toPageData(page);
    }

    @GetMapping("/binding/template/download")
    @ApiOperation("下载导入绑定信息模版")
    public ResponseEntity<byte[]> downloadTemplate() throws Exception {
        final String TEMPLATE_FILE_NAME = "Binding.xls";
        log.debug("REST request to download template excel file !");
        InputStream template = this.getClass().getResourceAsStream("/templates/excel/" + TEMPLATE_FILE_NAME);
        byte[] files = IOUtils.toByteArray(template);
        return download(files, TEMPLATE_FILE_NAME);
    }

    private ResponseEntity<byte[]> download(byte[] content, String fileName) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Content-Disposition",
            "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        return new ResponseEntity<>(content, headers, HttpStatus.CREATED);
    }

}
