package com.joinbe.web.rest;

import com.joinbe.service.DictTypeService;
import com.joinbe.service.dto.DictEntryDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing {@link com.joinbe.domain.DictType}.
 */
@RestController
@RequestMapping("/api")
@Api(value ="数据字典相关接口", tags={"数据字典相关接口"}, produces = "application/json" )
public class DictTypeResource {

    private final Logger log = LoggerFactory.getLogger(DictTypeResource.class);

    private final DictTypeService dictTypeService;


    public DictTypeResource(DictTypeService dictTypeService) {
        this.dictTypeService = dictTypeService;
    }

//    /**
//     * {@code POST  /dict-types} : Create a new dictType.
//     *
//     * @param dictTypeDTO the dictTypeDTO to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dictTypeDTO, or with status {@code 400 (Bad Request)} if the dictType has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/dict-types")
//    public ResponseEntity<DictTypeDTO> createDictType(@Valid @RequestBody DictTypeDTO dictTypeDTO) throws URISyntaxException {
//        log.debug("REST request to save DictType : {}", dictTypeDTO);
//        if (dictTypeDTO.getId() != null) {
//            throw new BadRequestAlertException("A new dictType cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        DictTypeDTO result = dictTypeService.save(dictTypeDTO);
//        return ResponseEntity.created(new URI("/api/dict-types/" + result.getId()))
//            .body(result);
//    }

//    /**
//     * {@code PUT  /dict-types} : Updates an existing dictType.
//     *
//     * @param dictTypeDTO the dictTypeDTO to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dictTypeDTO,
//     * or with status {@code 400 (Bad Request)} if the dictTypeDTO is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the dictTypeDTO couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/dict-types")
//    public ResponseEntity<DictTypeDTO> updateDictType(@Valid @RequestBody DictTypeDTO dictTypeDTO) throws URISyntaxException {
//        log.debug("REST request to update DictType : {}", dictTypeDTO);
//        if (dictTypeDTO.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        DictTypeDTO result = dictTypeService.save(dictTypeDTO);
//        return ResponseEntity.ok()
//            .body(result);
//    }
//
//    /**
//     * {@code GET  /dict-types} : get all the dictTypes.
//     *
//     * @param pageable the pagination information.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dictTypes in body.
//     */
//    @GetMapping("/dict-types")
//    public ResponseEntity<PageData<DictTypeDTO>> getAllDictTypes(Pageable pageable) {
//        log.debug("REST request to get a page of DictTypes");
//        Page<DictTypeDTO> page = dictTypeService.findAll(pageable);
//        return ResponseUtil.toPageData(page);
//    }

    /**
     * {@code GET  /diction/:type} : get values fo the dictType type.
     *
     * @param type the type of the diction to retrieve.
     * @return a list of diction entries for the type
     */
    @ApiOperation("获取数据字典值")
    @GetMapping("/dict-types/{type}")
    public List<DictEntryDTO> getDictionEntryByType(@PathVariable  @ApiParam(value="数据字典key") String type) {
        log.debug("REST request to get DictType : {}", type);
        return dictTypeService.getDictEntriesByType(type);
    }

//    /**
//     * {@code DELETE  /dict-types/:id} : delete the "id" dictType.
//     *
//     * @param id the id of the dictTypeDTO to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/dict-types/{id}")
//    public ResponseEntity<Void> deleteDictType(@PathVariable Long id) {
//        log.debug("REST request to delete DictType : {}", id);
//        dictTypeService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}
