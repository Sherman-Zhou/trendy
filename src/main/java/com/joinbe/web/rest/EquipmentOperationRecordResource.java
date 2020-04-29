package com.joinbe.web.rest;

import com.joinbe.service.EquipmentOperationRecordService;
import com.joinbe.service.dto.EquipmentOperationRecordDTO;
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
 * REST controller for managing {@link com.joinbe.domain.EquipmentOperationRecord}.
 */
@RestController
@RequestMapping("/api")
public class EquipmentOperationRecordResource {

    private static final String ENTITY_NAME = "equipmentOperationRecord";
    private final Logger log = LoggerFactory.getLogger(EquipmentOperationRecordResource.class);
    private final EquipmentOperationRecordService equipmentOperationRecordService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public EquipmentOperationRecordResource(EquipmentOperationRecordService equipmentOperationRecordService) {
        this.equipmentOperationRecordService = equipmentOperationRecordService;
    }

    /**
     * {@code POST  /equipment-operation-records} : Create a new equipmentOperationRecord.
     *
     * @param equipmentOperationRecordDTO the equipmentOperationRecordDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new equipmentOperationRecordDTO, or with status {@code 400 (Bad Request)} if the equipmentOperationRecord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/equipment-operation-records")
    public ResponseEntity<EquipmentOperationRecordDTO> createEquipmentOperationRecord(@Valid @RequestBody EquipmentOperationRecordDTO equipmentOperationRecordDTO) throws URISyntaxException {
        log.debug("REST request to save EquipmentOperationRecord : {}", equipmentOperationRecordDTO);
        if (equipmentOperationRecordDTO.getId() != null) {
            throw new BadRequestAlertException("A new equipmentOperationRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EquipmentOperationRecordDTO result = equipmentOperationRecordService.save(equipmentOperationRecordDTO);
        return ResponseEntity.created(new URI("/api/equipment-operation-records/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /equipment-operation-records} : Updates an existing equipmentOperationRecord.
     *
     * @param equipmentOperationRecordDTO the equipmentOperationRecordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentOperationRecordDTO,
     * or with status {@code 400 (Bad Request)} if the equipmentOperationRecordDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the equipmentOperationRecordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/equipment-operation-records")
    public ResponseEntity<EquipmentOperationRecordDTO> updateEquipmentOperationRecord(@Valid @RequestBody EquipmentOperationRecordDTO equipmentOperationRecordDTO) throws URISyntaxException {
        log.debug("REST request to update EquipmentOperationRecord : {}", equipmentOperationRecordDTO);
        if (equipmentOperationRecordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EquipmentOperationRecordDTO result = equipmentOperationRecordService.save(equipmentOperationRecordDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, equipmentOperationRecordDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /equipment-operation-records} : get all the equipmentOperationRecords.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of equipmentOperationRecords in body.
     */
    @GetMapping("/equipment-operation-records")
    public ResponseEntity<List<EquipmentOperationRecordDTO>> getAllEquipmentOperationRecords(Pageable pageable) {
        log.debug("REST request to get a page of EquipmentOperationRecords");
        Page<EquipmentOperationRecordDTO> page = equipmentOperationRecordService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
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

    /**
     * {@code DELETE  /equipment-operation-records/:id} : delete the "id" equipmentOperationRecord.
     *
     * @param id the id of the equipmentOperationRecordDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/equipment-operation-records/{id}")
    public ResponseEntity<Void> deleteEquipmentOperationRecord(@PathVariable Long id) {
        log.debug("REST request to delete EquipmentOperationRecord : {}", id);
        equipmentOperationRecordService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
