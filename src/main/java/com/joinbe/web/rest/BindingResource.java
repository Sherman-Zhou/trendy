package com.joinbe.web.rest;

import com.joinbe.common.excel.BindingData;
import com.joinbe.common.excel.BindingDataListener;
import com.joinbe.common.util.ExcelUtil;
import com.joinbe.config.Constants;
import com.joinbe.data.collector.store.RedissonEquipmentStore;
import com.joinbe.domain.Staff;
import com.joinbe.domain.Vehicle;
import com.joinbe.domain.enumeration.VehicleStatusEnum;
import com.joinbe.repository.CityRepository;
import com.joinbe.repository.StaffRepository;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.VehicleService;
import com.joinbe.service.dto.DivisionDTO;
import com.joinbe.service.dto.EquipmentDTO;
import com.joinbe.service.dto.UploadResponse;
import com.joinbe.service.dto.VehicleDetailsDTO;
import com.joinbe.service.util.SpringContextUtils;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.EquipmentVehicleBindingVM;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import com.joinbe.web.rest.vm.VehicleBindingVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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


    private final EquipmentService equipmentService;

    private final MessageSource messageSource;

    private final RedissonEquipmentStore redissonEquipmentStore;

    private final StaffRepository staffRepository;

    private final CityRepository cityRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public BindingResource(VehicleService vehicleService,
                           EquipmentService equipmentService, MessageSource messageSource, RedissonEquipmentStore redissonEquipmentStore,
                           StaffRepository staffRepository, CityRepository cityRepository) {
        this.vehicleService = vehicleService;
        this.equipmentService = equipmentService;
        this.messageSource = messageSource;
        this.redissonEquipmentStore = redissonEquipmentStore;
        this.staffRepository = staffRepository;
        this.cityRepository = cityRepository;
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
        return equipmentService.findAllUnboundEquipments();
    }

    /**
     * {@code GET  /binding/vehicle/:id/unbound} : get the "id" vehicle.
     *
     * @param id the id of the vehicleDTO to unbound.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/binding/vehicle/{id}/unbound")
    @ApiOperation("车辆解绑设备")
    public ResponseEntity<VehicleDetailsDTO> unbound(@PathVariable @ApiParam(value = "车辆主键", required = true) Long id) {
        log.debug("REST request to get Vehicle : {}", id);
        Optional<VehicleDetailsDTO> vehicleDTO = vehicleService.unbound(id);
        return ResponseUtil.wrapOrNotFound(vehicleDTO);
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
     * @param vehicleId the id of the vehicle to test.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the message}.
     */
    @GetMapping("/binding/{vehicleId}/conn-testing")
    @ApiOperation("连通测试")
    public ResponseEntity<String> connTesting(@PathVariable @ApiParam(value = "车辆主键", required = true)  Long vehicleId) {
        log.debug("Connection testing with vehicle: {}", vehicleId);
        Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
        String deviceId;
        if (vehicle.isPresent() && vehicle.get().getEquipment() != null) {
            deviceId = vehicle.get().getEquipment().getImei();
        } else {
            return ResponseEntity.ok().body("No binding device found for the vehicle ID:" + vehicleId);
        }
        VehicleStatusEnum currentDeviceStatus = redissonEquipmentStore.getDeviceStatus(deviceId);
        if(currentDeviceStatus != null && !VehicleStatusEnum.UNKNOWN.equals(currentDeviceStatus)){
            return ResponseEntity.ok().body("Successful");
        }
        return ResponseEntity.ok().body("Device is offline");
    }

    @GetMapping("/binding/user/divisions")
    @ApiOperation("获取当前用户部门")
    public  List<DivisionDTO> getUserDivisions() {
        Optional<Staff> staffOptional = staffRepository.findOneWithShopsAndCitiesByLogin(SecurityUtils.getCurrentUserLogin().get());
        if (staffOptional.isPresent()) {
            Staff staff = staffOptional.get();
            List<DivisionDTO> divisionDTOS = staff.getShops().stream().map(shop -> new DivisionDTO(shop, LocaleContextHolder.getLocale())).collect(Collectors.toList());

            List<DivisionDTO> divisionDTOInCities = staff.getCities().stream().map(shop -> new DivisionDTO(shop, LocaleContextHolder.getLocale())).collect(Collectors.toList());
            divisionDTOS.addAll(divisionDTOInCities);

            Map<String, List<DivisionDTO>> children = divisionDTOS.stream().filter(divisionDTO -> StringUtils.isNotEmpty(divisionDTO.getParentId()))
                .collect(Collectors.groupingBy(DivisionDTO::getParentId));

            boolean hasNotRootCity = divisionDTOS.stream().noneMatch(divisionDTO -> Constants.CITY_ROOT_ID.equals(divisionDTO.getId()));

            if (hasNotRootCity && children.get(Constants.CITY_ROOT_ID) != null) {
                divisionDTOS.add(new DivisionDTO(cityRepository.findById(Constants.CITY_ROOT_ID).get(), LocaleContextHolder.getLocale()));
            }

            for (DivisionDTO divisionDTO : divisionDTOS) {
                if (children.get(divisionDTO.getId()) != null) {
                    divisionDTO.setChildren(children.get(divisionDTO.getId()));
                }
            }
            return divisionDTOS.stream().filter(menu -> StringUtils.isEmpty(menu.getParentId())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    @PostMapping("/binding/upload")
    @ApiOperation("导入绑定信息")
    public UploadResponse uploadAndBinding(@RequestParam("file") MultipartFile file) {
        log.debug("uploaded file: {}", file.getOriginalFilename());
        BindingDataListener bindingDataListener = SpringContextUtils.getBean(BindingDataListener.class);
        try {
            ExcelUtil.readExcel(file.getInputStream(), BindingData.class, bindingDataListener);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e.getCause() != null && e.getCause() instanceof BadRequestAlertException) {
                throw (BadRequestAlertException) e.getCause();
            }
            throw new RuntimeException(e);
        }
        vehicleService.binding(bindingDataListener.getResponse(), bindingDataListener.getList());

        return bindingDataListener.getResponse();
    }

    @GetMapping("/binding/template/download")
    @ApiOperation("下载导入绑定信息模版")
    public ResponseEntity<byte[]> downloadTemplate() throws Exception {
        final String TEMPLATE_FILE_NAME = "Binding.xlsx";
        log.debug("REST request to download template excel file !");
        String fileName = messageSource.getMessage("binding.upload.template", null, null, LocaleContextHolder.getLocale());
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
