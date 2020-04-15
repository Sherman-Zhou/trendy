package com.joinbe.web.rest;

import com.joinbe.service.DictEntryService;
import com.joinbe.service.dto.DictEntryDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * REST controller for managing {@link com.joinbe.domain.DictEntry}.
 */
@RestController
@RequestMapping("/api")
public class DictEntryResource {

    private final Logger log = LoggerFactory.getLogger(DictEntryResource.class);

    private static final String ENTITY_NAME = "dictEntry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DictEntryService dictEntryService;

    public DictEntryResource(DictEntryService dictEntryService) {
        this.dictEntryService = dictEntryService;
    }

    /**
     * {@code POST  /dict-entries} : Create a new dictEntry.
     *
     * @param dictEntryDTO the dictEntryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dictEntryDTO, or with status {@code 400 (Bad Request)} if the dictEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/dict-entries")
    public ResponseEntity<DictEntryDTO> createDictEntry(@Valid @RequestBody DictEntryDTO dictEntryDTO) throws URISyntaxException {
        log.debug("REST request to save DictEntry : {}", dictEntryDTO);
        if (dictEntryDTO.getId() != null) {
            throw new BadRequestAlertException("A new dictEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DictEntryDTO result = dictEntryService.save(dictEntryDTO);
        return ResponseEntity.created(new URI("/api/dict-entries/" + result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /dict-entries} : Updates an existing dictEntry.
     *
     * @param dictEntryDTO the dictEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dictEntryDTO,
     * or with status {@code 400 (Bad Request)} if the dictEntryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dictEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/dict-entries")
    public ResponseEntity<DictEntryDTO> updateDictEntry(@Valid @RequestBody DictEntryDTO dictEntryDTO) throws URISyntaxException {
        log.debug("REST request to update DictEntry : {}", dictEntryDTO);
        if (dictEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DictEntryDTO result = dictEntryService.save(dictEntryDTO);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * {@code GET  /dict-entries} : get all the dictEntries.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dictEntries in body.
     */
    @GetMapping("/dict-entries")
    public ResponseEntity<PageData<DictEntryDTO>> getAllDictEntries(Pageable pageable) {
        log.debug("REST request to get a page of DictEntries");
        Page<DictEntryDTO> page = dictEntryService.findAll(pageable);
        return ResponseUtil.toPageData(page);
    }

    /**
     * {@code GET  /dict-entries/:id} : get the "id" dictEntry.
     *
     * @param id the id of the dictEntryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dictEntryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/dict-entries/{id}")
    public ResponseEntity<DictEntryDTO> getDictEntry(@PathVariable Long id) {
        log.debug("REST request to get DictEntry : {}", id);
        Optional<DictEntryDTO> dictEntryDTO = dictEntryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dictEntryDTO);
    }

    /**
     * {@code DELETE  /dict-entries/:id} : delete the "id" dictEntry.
     *
     * @param id the id of the dictEntryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/dict-entries/{id}")
    public ResponseEntity<Void> deleteDictEntry(@PathVariable Long id) {
        log.debug("REST request to delete DictEntry : {}", id);
        dictEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
