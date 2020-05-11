package com.joinbe.web.rest;

import com.joinbe.service.EquipmentConfigService;
import com.joinbe.service.dto.EquipmentConfigDTO;
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
 * REST controller for managing {@link com.joinbe.domain.EquipmentConfig}.
 */
//@RestController
@RequestMapping("/api")
public class EquipmentConfigResource {

    private static final String ENTITY_NAME = "equipmentConfig";
    private final Logger log = LoggerFactory.getLogger(EquipmentConfigResource.class);
    private final EquipmentConfigService equipmentConfigService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public EquipmentConfigResource(EquipmentConfigService equipmentConfigService) {
        this.equipmentConfigService = equipmentConfigService;
    }

    /**
     * {@code POST  /equipment-configs} : Create a new equipmentConfig.
     *
     * @param equipmentConfigDTO the equipmentConfigDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new equipmentConfigDTO, or with status {@code 400 (Bad Request)} if the equipmentConfig has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/equipment-configs")
    public ResponseEntity<EquipmentConfigDTO> createEquipmentConfig(@Valid @RequestBody EquipmentConfigDTO equipmentConfigDTO) throws URISyntaxException {
        log.debug("REST request to save EquipmentConfig : {}", equipmentConfigDTO);
        if (equipmentConfigDTO.getId() != null) {
            throw new BadRequestAlertException("A new equipmentConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EquipmentConfigDTO result = equipmentConfigService.save(equipmentConfigDTO);
        return ResponseEntity.created(new URI("/api/equipment-configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /equipment-configs} : Updates an existing equipmentConfig.
     *
     * @param equipmentConfigDTO the equipmentConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentConfigDTO,
     * or with status {@code 400 (Bad Request)} if the equipmentConfigDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the equipmentConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/equipment-configs")
    public ResponseEntity<EquipmentConfigDTO> updateEquipmentConfig(@Valid @RequestBody EquipmentConfigDTO equipmentConfigDTO) throws URISyntaxException {
        log.debug("REST request to update EquipmentConfig : {}", equipmentConfigDTO);
        if (equipmentConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EquipmentConfigDTO result = equipmentConfigService.save(equipmentConfigDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, equipmentConfigDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /equipment-configs} : get all the equipmentConfigs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of equipmentConfigs in body.
     */
    @GetMapping("/equipment-configs")
    public ResponseEntity<List<EquipmentConfigDTO>> getAllEquipmentConfigs(Pageable pageable) {
        log.debug("REST request to get a page of EquipmentConfigs");
        Page<EquipmentConfigDTO> page = equipmentConfigService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /equipment-configs/:id} : get the "id" equipmentConfig.
     *
     * @param id the id of the equipmentConfigDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the equipmentConfigDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/equipment-configs/{id}")
    public ResponseEntity<EquipmentConfigDTO> getEquipmentConfig(@PathVariable Long id) {
        log.debug("REST request to get EquipmentConfig : {}", id);
        Optional<EquipmentConfigDTO> equipmentConfigDTO = equipmentConfigService.findOne(id);
        return ResponseUtil.wrapOrNotFound(equipmentConfigDTO);
    }

    /**
     * {@code DELETE  /equipment-configs/:id} : delete the "id" equipmentConfig.
     *
     * @param id the id of the equipmentConfigDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/equipment-configs/{id}")
    public ResponseEntity<Void> deleteEquipmentConfig(@PathVariable Long id) {
        log.debug("REST request to delete EquipmentConfig : {}", id);
        equipmentConfigService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
