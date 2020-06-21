package com.joinbe.web.rest;

import com.joinbe.service.EquipmentOperationRecordService;
import com.joinbe.service.dto.EquipmentOperationRecordDTO;
import com.joinbe.web.rest.vm.EquipmentOpRecordVM;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST controller for managing {@link com.joinbe.domain.EquipmentOperationRecord}.
 */
@RestController
@RequestMapping("/api")
@Api(value = "设备操作记录相关接口", tags = {"设备操作记录相关接口"})
public class EquipmentOperationRecordResource {

    private static final String ENTITY_NAME = "equipmentOperationRecord";
    private final Logger log = LoggerFactory.getLogger(EquipmentOperationRecordResource.class);
    private final EquipmentOperationRecordService equipmentOperationRecordService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public EquipmentOperationRecordResource(EquipmentOperationRecordService equipmentOperationRecordService) {
        this.equipmentOperationRecordService = equipmentOperationRecordService;
    }

//    /**
//     * {@code POST  /equipment-operation-records} : Create a new equipmentOperationRecord.
//     *
//     * @param equipmentOperationRecordDTO the equipmentOperationRecordDTO to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new equipmentOperationRecordDTO, or with status {@code 400 (Bad Request)} if the equipmentOperationRecord has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/equipment-operation-records")
//    public ResponseEntity<EquipmentOperationRecordDTO> createEquipmentOperationRecord(@Valid @RequestBody EquipmentOperationRecordDTO equipmentOperationRecordDTO) throws URISyntaxException {
//        log.debug("REST request to save EquipmentOperationRecord : {}", equipmentOperationRecordDTO);
//        if (equipmentOperationRecordDTO.getId() != null) {
//            throw new BadRequestAlertException("A new equipmentOperationRecord cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        EquipmentOperationRecordDTO result = equipmentOperationRecordService.save(equipmentOperationRecordDTO);
//        return ResponseEntity.created(new URI("/api/equipment-operation-records/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * {@code PUT  /equipment-operation-records} : Updates an existing equipmentOperationRecord.
//     *
//     * @param equipmentOperationRecordDTO the equipmentOperationRecordDTO to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentOperationRecordDTO,
//     * or with status {@code 400 (Bad Request)} if the equipmentOperationRecordDTO is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the equipmentOperationRecordDTO couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/equipment-operation-records")
//    public ResponseEntity<EquipmentOperationRecordDTO> updateEquipmentOperationRecord(@Valid @RequestBody EquipmentOperationRecordDTO equipmentOperationRecordDTO) throws URISyntaxException {
//        log.debug("REST request to update EquipmentOperationRecord : {}", equipmentOperationRecordDTO);
//        if (equipmentOperationRecordDTO.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        EquipmentOperationRecordDTO result = equipmentOperationRecordService.save(equipmentOperationRecordDTO);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, equipmentOperationRecordDTO.getId().toString()))
//            .body(result);
//    }

    /**
     * {@code GET  /equipment-operation-records} : get all the equipmentOperationRecords.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of equipmentOperationRecords in body.
     */
    @GetMapping("/equipment-operation-records")
    public ResponseEntity<PageData<EquipmentOperationRecordDTO>> getAllEquipmentOperationRecords(Pageable pageable, EquipmentOpRecordVM vm) {
        log.debug("REST request to get a page of EquipmentOperationRecords");
        Page<EquipmentOperationRecordDTO> page = equipmentOperationRecordService.findAll(pageable, vm);

        return ResponseUtil.toPageData(page);
    }

    /**
     * {@code GET  /equipment-operation-records/:id} : get the "id" equipmentOperationRecord.
     *
     * @param id the id of the equipmentOperationRecordDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the equipmentOperationRecordDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/equipment-operation-records/{id}")
    public ResponseEntity<EquipmentOperationRecordDTO> getEquipmentOperationRecord(@PathVariable Long id) {
        log.debug("REST request to get EquipmentOperationRecord : {}", id);
        Optional<EquipmentOperationRecordDTO> equipmentOperationRecordDTO = equipmentOperationRecordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(equipmentOperationRecordDTO);
    }

}
