package com.joinbe.web.rest;

import com.joinbe.service.EquipmentFaultService;
import com.joinbe.service.dto.EquipmentFaultDTO;
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
 * REST controller for managing {@link com.joinbe.domain.EquipmentFault}.
 */
//@RestController
@RequestMapping("/api")
public class EquipmentFaultResource {

    private static final String ENTITY_NAME = "equipmentFault";
    private final Logger log = LoggerFactory.getLogger(EquipmentFaultResource.class);
    private final EquipmentFaultService equipmentFaultService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public EquipmentFaultResource(EquipmentFaultService equipmentFaultService) {
        this.equipmentFaultService = equipmentFaultService;
    }

    /**
     * {@code POST  /equipment-faults} : Create a new equipmentFault.
     *
     * @param equipmentFaultDTO the equipmentFaultDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new equipmentFaultDTO, or with status {@code 400 (Bad Request)} if the equipmentFault has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/equipment-faults")
    public ResponseEntity<EquipmentFaultDTO> createEquipmentFault(@Valid @RequestBody EquipmentFaultDTO equipmentFaultDTO) throws URISyntaxException {
        log.debug("REST request to save EquipmentFault : {}", equipmentFaultDTO);
        if (equipmentFaultDTO.getId() != null) {
            throw new BadRequestAlertException("A new equipmentFault cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EquipmentFaultDTO result = equipmentFaultService.save(equipmentFaultDTO);
        return ResponseEntity.created(new URI("/api/equipment-faults/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /equipment-faults} : Updates an existing equipmentFault.
     *
     * @param equipmentFaultDTO the equipmentFaultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentFaultDTO,
     * or with status {@code 400 (Bad Request)} if the equipmentFaultDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the equipmentFaultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/equipment-faults")
    public ResponseEntity<EquipmentFaultDTO> updateEquipmentFault(@Valid @RequestBody EquipmentFaultDTO equipmentFaultDTO) throws URISyntaxException {
        log.debug("REST request to update EquipmentFault : {}", equipmentFaultDTO);
        if (equipmentFaultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EquipmentFaultDTO result = equipmentFaultService.save(equipmentFaultDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, equipmentFaultDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /equipment-faults} : get all the equipmentFaults.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of equipmentFaults in body.
     */
    @GetMapping("/equipment-faults")
    public ResponseEntity<List<EquipmentFaultDTO>> getAllEquipmentFaults(Pageable pageable) {
        log.debug("REST request to get a page of EquipmentFaults");
        Page<EquipmentFaultDTO> page = equipmentFaultService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /equipment-faults/:id} : get the "id" equipmentFault.
     *
     * @param id the id of the equipmentFaultDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the equipmentFaultDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/equipment-faults/{id}")
    public ResponseEntity<EquipmentFaultDTO> getEquipmentFault(@PathVariable Long id) {
        log.debug("REST request to get EquipmentFault : {}", id);
        Optional<EquipmentFaultDTO> equipmentFaultDTO = equipmentFaultService.findOne(id);
        return ResponseUtil.wrapOrNotFound(equipmentFaultDTO);
    }

    /**
     * {@code DELETE  /equipment-faults/:id} : delete the "id" equipmentFault.
     *
     * @param id the id of the equipmentFaultDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/equipment-faults/{id}")
    public ResponseEntity<Void> deleteEquipmentFault(@PathVariable Long id) {
        log.debug("REST request to delete EquipmentFault : {}", id);
        equipmentFaultService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
