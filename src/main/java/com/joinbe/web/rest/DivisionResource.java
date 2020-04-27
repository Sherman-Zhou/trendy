package com.joinbe.web.rest;

import com.joinbe.service.DivisionService;
import com.joinbe.service.dto.DivisionDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.DivisionVM;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.joinbe.domain.Division}.
 */
@RestController
@RequestMapping("/api")
public class DivisionResource {

    private final Logger log = LoggerFactory.getLogger(DivisionResource.class);

    private static final String ENTITY_NAME = "division";

    private final DivisionService divisionService;

    public DivisionResource(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    /**
     * {@code POST  /divisions} : Create a new division.
     *
     * @param divisionDTO the divisionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new divisionDTO, or with status {@code 400 (Bad Request)} if the division has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/divisions")
    public ResponseEntity<DivisionDTO> createDivision(@Valid @RequestBody DivisionDTO divisionDTO) throws URISyntaxException {
        log.debug("REST request to save Division : {}", divisionDTO);
        if (divisionDTO.getId() != null) {
            throw new BadRequestAlertException("A new division cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DivisionDTO result = divisionService.save(divisionDTO);
        return ResponseEntity.created(new URI("/api/divisions/" + result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /divisions} : Updates an existing division.
     *
     * @param divisionDTO the divisionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated divisionDTO,
     * or with status {@code 400 (Bad Request)} if the divisionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the divisionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/divisions")
    public ResponseEntity<DivisionDTO> updateDivision(@Valid @RequestBody DivisionDTO divisionDTO) {
        log.debug("REST request to update Division : {}", divisionDTO);
        if (divisionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id or version", ENTITY_NAME, "idnull");
        }
        DivisionDTO result = divisionService.save(divisionDTO);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * {@code GET  /divisions} : get all the divisions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of divisions in body.
     */
    @GetMapping("/divisions")
    public ResponseEntity<PageData<DivisionDTO>> getAllDivisions(Pageable pageable, DivisionVM vm) {
        log.debug("REST request to get a page of Divisions");
        Page<DivisionDTO> page = divisionService.findAll(pageable, vm);
        return ResponseUtil.toPageData(page);
    }

    /**
     * GET  /divisions/:parentId/children : get all the child divisions.
     *
     * @param parentId the parentId
     * @return the ResponseEntity with status 200 (OK) and the list of divisions in body
     */
    @GetMapping("/divisions/{parentId}/children")
    public List<DivisionDTO> getAllSubDepartments(@PathVariable Long parentId) {
        log.debug("REST request to get a List of child DivisionDTO for parent:{}", parentId);
        return divisionService.findAllByParentId(parentId);
    }

    /**
     * {@code GET  /divisions/:id} : get the "id" division.
     *
     * @param id the id of the divisionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the divisionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/divisions/{id}")
    public ResponseEntity<DivisionDTO> getDivision(@PathVariable Long id) {
        log.debug("REST request to get Division : {}", id);
        Optional<DivisionDTO> divisionDTO = divisionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(divisionDTO);
    }

    /**
     * {@code DELETE  /divisions/:id} : delete the "id" division.
     *
     * @param id the id of the divisionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/divisions/{id}")
    public ResponseEntity<Void> deleteDivision(@PathVariable Long id) {
        log.debug("REST request to delete Division : {}", id);
        divisionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
