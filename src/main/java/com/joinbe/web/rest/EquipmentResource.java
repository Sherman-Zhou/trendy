package com.joinbe.web.rest;

import com.joinbe.service.EquipmentService;
import com.joinbe.service.dto.EquipmentDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.EquipmentVM;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
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

import javax.validation.Valid;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Optional;

/**
 * REST controller for managing {@link com.joinbe.domain.Equipment}.
 */
@RestController
@RequestMapping("/api")
@Api(value = "设备管理相关接口", tags = {"设备管理相关接口"})
public class EquipmentResource {

    private static final String ENTITY_NAME = "equipment";
    private final Logger log = LoggerFactory.getLogger(EquipmentResource.class);
    private final EquipmentService equipmentService;


    public EquipmentResource(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    /**
     * {@code POST  /equipment} : Create a new equipment.
     *
     * @param equipmentDTO the equipmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new equipmentDTO, or with status {@code 400 (Bad Request)} if the equipment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/equipment")
    @ApiOperation("创建设备")
    public ResponseEntity<EquipmentDTO> createEquipment(@Valid @RequestBody EquipmentDTO equipmentDTO) throws URISyntaxException {
        log.debug("REST request to save Equipment : {}", equipmentDTO);
        if (equipmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new equipment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EquipmentDTO result = equipmentService.save(equipmentDTO);
        return ResponseEntity.created(new URI("/api/equipment/" + result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /equipment} : Updates an existing equipment.
     *
     * @param equipmentDTO the equipmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentDTO,
     * or with status {@code 400 (Bad Request)} if the equipmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the equipmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/equipment")
    @ApiOperation("修改设备")
    public ResponseEntity<EquipmentDTO> updateEquipment(@Valid @RequestBody EquipmentDTO equipmentDTO) throws URISyntaxException {
        log.debug("REST request to update Equipment : {}", equipmentDTO);
        if (equipmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EquipmentDTO result = equipmentService.save(equipmentDTO);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * {@code GET  /equipment} : get all the equipment.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of equipment in body.
     */
    @GetMapping("/equipment")
    @ApiOperation("搜索设备")
    public ResponseEntity<PageData<EquipmentDTO>> getAllEquipment(Pageable pageable, EquipmentVM vm) {
        log.debug("REST request to get a page of Equipment");
        Page<EquipmentDTO> page = equipmentService.findAll(pageable, vm);
        return ResponseUtil.toPageData(page);
    }

    /**
     * {@code GET  /equipment/:id} : get the "id" equipment.
     *
     * @param id the id of the equipmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the equipmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/equipment/{id}")
    @ApiOperation("获取设备详情")
    public ResponseEntity<EquipmentDTO> getEquipment(@PathVariable Long id) {
        log.debug("REST request to get Equipment : {}", id);
        Optional<EquipmentDTO> equipmentDTO = equipmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(equipmentDTO);
    }

    /**
     * {@code DELETE  /equipment/:id} : delete the "id" equipment.
     *
     * @param id the id of the equipmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/equipment/{id}")
    @ApiOperation("删除设备")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        log.debug("REST request to delete Equipment : {}", id);
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/equipment/upload")
    @ApiOperation("导入excel")
    public ResponseEntity<PageData<EquipmentDTO>> upload(@RequestParam("file") MultipartFile file, Pageable pageable) {
        log.debug("uploaded file: {}", file.getOriginalFilename());
        Page<EquipmentDTO> page = equipmentService.findAll(pageable, new EquipmentVM());
        return ResponseUtil.toPageData(page);
    }

    @GetMapping("/equipment/template/download")
    @ApiOperation("下载导入excel模版")
    public ResponseEntity<byte[]> downloadTemplate() throws Exception {
        final String TEMPLATE_FILE_NAME = "Equipment.xls";
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
