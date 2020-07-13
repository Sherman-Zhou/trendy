package com.joinbe.web.rest;

import com.joinbe.service.MerchantService;
import com.joinbe.service.dto.MerchantDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Api(value = "平台管理相关接口", tags = {"平台管理相关接口"}, produces = "application/json")
public class MerchantResource {
    private static final String ENTITY_NAME = "merchant";
    private final Logger log = LoggerFactory.getLogger(MerchantResource.class);
    private final MerchantService merchantService;

    public MerchantResource(MerchantService merchantService) {
        this.merchantService = merchantService;
    }


    /**
     * {@code POST  /merchants} : Create a new merchant.
     *
     * @param merchantDTO the merchantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new merchantDTO,
     * or with status {@code 400 (Bad Request)} if the merchant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/merchants")
    @ApiOperation("创建平台")
    public ResponseEntity<MerchantDTO> createRole(@Valid @RequestBody MerchantDTO merchantDTO) throws URISyntaxException {
        log.debug("REST request to save Merchant : {}", merchantDTO);
        if (merchantDTO.getId() != null) {
            throw new BadRequestAlertException("A new role cannot already have an ID", ENTITY_NAME, "idexists");
        } else if (merchantService.findByName(merchantDTO.getName()).isPresent()) {
            throw new BadRequestAlertException("merchant name already used!", ENTITY_NAME, "nameexists");
        }
        MerchantDTO result = merchantService.save(merchantDTO);
        return ResponseEntity.created(new URI("/api/merchants/" + result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /merchants} : Updates an existing role.
     *
     * @param merchantDTO the merchantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated merchantDTO,
     * or with status {@code 400 (Bad Request)} if the merchantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the merchantDTO couldn't be updated.
     */
    @PutMapping("/merchants")
    @ApiOperation("更新平台")
    public ResponseEntity<MerchantDTO> updateRole(@Valid @RequestBody MerchantDTO merchantDTO) {
        log.debug("REST request to update Role : {}", merchantDTO);
        if (merchantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        } else if (merchantService.findByName(merchantDTO.getName()).isPresent()) {
            throw new BadRequestAlertException("merchant name already used!", ENTITY_NAME, "nameexists");
        }
        MerchantDTO result = merchantService.save(merchantDTO);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * {@code GET  /merchants} : get all the merchants.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of merchants in body.
     */
    @GetMapping("/merchants")
    @ApiOperation("搜索平台")
    public ResponseEntity<PageData<MerchantDTO>> getAllMerchants(Pageable pageable) {
        log.debug("REST request to get a page of merchants");
        Page<MerchantDTO> page = merchantService.findAll(pageable);

        return ResponseUtil.toPageData(page);
    }

    /**
     * {@code DELETE  /merchants/:id} : delete the "id" merchant.
     *
     * @param id the id of the merchantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @ApiOperation("删除平台")
    @DeleteMapping("/merchants/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable @ApiParam(value = "平台主键", required = true) Long id) {
        log.debug("REST request to delete Role : {}", id);
        merchantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET  /merchants/:id} : get the "id" role.
     *
     * @param id the id of the merchantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/merchants/{id}")
    @ApiOperation("获取平台详情")
    public ResponseEntity<MerchantDTO> getRole(@PathVariable @ApiParam(value = "平台主键", required = true) Long id) {
        log.debug("REST request to get Role : {}", id);
        Optional<MerchantDTO> roleDTO = merchantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(roleDTO);
    }
}

