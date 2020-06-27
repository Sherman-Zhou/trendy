package com.joinbe.web.rest;

import com.joinbe.common.excel.BindingData;
import com.joinbe.common.excel.BindingDataListener;
import com.joinbe.common.util.ExcelUtil;
import com.joinbe.domain.Vehicle;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.DivisionService;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.VehicleService;
import com.joinbe.service.dto.DivisionDTO;
import com.joinbe.service.dto.EquipmentDTO;
import com.joinbe.service.dto.UploadResultDTO;
import com.joinbe.service.dto.VehicleDetailsDTO;
import com.joinbe.service.util.SpringContextUtils;
import com.joinbe.web.rest.vm.EquipmentVehicleBindingVM;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import com.joinbe.web.rest.vm.VehicleBindingVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link Vehicle}.
 */
@RestController
@RequestMapping("/api")
@Api(value = "车辆设备绑定相关接口", tags = {"车辆设备绑定相关接口"})
public class BindingResource {

    private final Logger log = LoggerFactory.getLogger(BindingResource.class);

    private final VehicleService vehicleService;

    private final DivisionService divisionService;

    private final EquipmentService equipmentService;

    private final MessageSource messageSource;

    public BindingResource(VehicleService vehicleService, DivisionService divisionService,
                           EquipmentService equipmentService, MessageSource messageSource) {
        this.vehicleService = vehicleService;
        this.divisionService = divisionService;
        this.equipmentService = equipmentService;
        this.messageSource = messageSource;
    }


    /**
     * {@code GET  /binding/vehicle/search} : get all the vehicles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicles in body.
     */
    @GetMapping("/binding/vehicle/search")
    @ApiOperation("搜索车辆")
    public ResponseEntity<PageData<VehicleDetailsDTO>> getAllVehicles(Pageable pageable, VehicleBindingVM vm) {
        log.debug("REST request to get a page of Vehicles");
        Page<VehicleDetailsDTO> page = vehicleService.findAll(pageable, vm);
        return ResponseUtil.toPageData(page);
    }


    @GetMapping("/binding/equipment/to-bound")
    @ApiOperation("待绑定设备")
    public List<EquipmentDTO> getAllUnboundEquipments() {
        log.debug("REST request to get a page of Vehicles");
        List<EquipmentDTO> list = equipmentService.findAllUnboundEquipments();
        return list;
    }

    /**
     * {@code Post  /binding} :bind vehicle and equipment.
     *
     * @param vm the id of the vehicle and equipment for binding.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}  or with status {@code 404 (Not Found)}.
     */
    @PostMapping("/binding")
    @ApiOperation("绑定车辆设备")
    public ResponseEntity<Void> binding(@RequestBody EquipmentVehicleBindingVM vm) {
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
    public ResponseEntity<String> connTesting(@PathVariable @ApiParam(value = "设备主键", required = true)  Long equipmentId) {
        log.debug("Connection testing with equipment: {}", equipmentId); //TODO: to implement...
        return ResponseEntity.ok().body("Successful");
    }

    @GetMapping("/binding/user/divisions")
    @ApiOperation("获取当前用户部门")
    public  List<DivisionDTO> getUserDivisions() {
        List<DivisionDTO> divisionDTOS = divisionService.findUserDivision(SecurityUtils.getCurrentUserLogin().get());

        Map<Long, List<DivisionDTO>> children = divisionDTOS.stream().filter(divisionDTO ->  divisionDTO.getParentId()!=null)
            .collect(Collectors.groupingBy(DivisionDTO::getParentId));
        for (DivisionDTO divisionDTO : divisionDTOS) {
            if (children.get(divisionDTO.getId()) != null) {
                divisionDTO.setChildren(children.get(divisionDTO.getId()));
            }
        }
        List<DivisionDTO> parents = divisionDTOS.stream().filter(menu -> menu.getParentId() == null).collect(Collectors.toList());
        return parents;
    }


    @PostMapping("/binding/upload")
    @ApiOperation("导入绑定信息")
    public List<UploadResultDTO> uploadAndBinding(@RequestParam("file") MultipartFile file) {
        log.debug("uploaded file: {}", file.getOriginalFilename());
        BindingDataListener bindingDataListener = SpringContextUtils.getBean(BindingDataListener.class);
        try {
            ExcelUtil.readExcel(file.getInputStream(), BindingData.class, bindingDataListener);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        List<UploadResultDTO> results = vehicleService.binding(bindingDataListener.getList());

        results.addAll(bindingDataListener.getErrors());

        return results.stream().sorted(Comparator.comparingLong(UploadResultDTO::getRowNum)).collect(Collectors.toList());
    }

    @GetMapping("/binding/template/download")
    @ApiOperation("下载导入绑定信息模版")
    public ResponseEntity<byte[]> downloadTemplate() throws Exception {
        final String TEMPLATE_FILE_NAME = "Binding.xlsx";
        log.debug("REST request to download template excel file !");
        String fileName = messageSource.getMessage("binding.upload.template", null, LocaleContextHolder.getLocale());
        InputStream template = this.getClass().getResourceAsStream("/templates/excel/" + TEMPLATE_FILE_NAME);
        byte[] files = IOUtils.toByteArray(template);
        return download(files, fileName + ".xlsx");
    }

    private ResponseEntity<byte[]> download(byte[] content, String fileName) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Content-Disposition",
            "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        return new ResponseEntity<>(content, headers, HttpStatus.CREATED);
    }

}
