package com.joinbe.web.rest;

import com.joinbe.service.EquipmentFaultService;
import com.joinbe.service.dto.EquipmentFaultDTO;
import com.joinbe.web.rest.vm.EquipmentFaultVM;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.joinbe.domain.EquipmentFault}.
 */
@RestController
@RequestMapping("/api")
@Api(value = "设备异常相关接口", tags = {"设备异常相关接口"})
public class EquipmentFaultResource {

    private static final String ENTITY_NAME = "equipmentFault";
    private final Logger log = LoggerFactory.getLogger(EquipmentFaultResource.class);
    private final EquipmentFaultService equipmentFaultService;


    public EquipmentFaultResource(EquipmentFaultService equipmentFaultService) {
        this.equipmentFaultService = equipmentFaultService;
    }


    @GetMapping("/equipment-faults/:id/read")
    @ApiOperation("设置设备异常已读")
    public ResponseEntity<EquipmentFaultDTO> readEquipmentFault(@PathVariable @ApiParam(value = "设备异常主键", required = true) Long id) {
        log.debug("REST request to update read status of EquipmentFault : {}", id);
        Optional<EquipmentFaultDTO> equipmentFaultDTO = equipmentFaultService.read(id);
        return ResponseUtil.wrapOrNotFound(equipmentFaultDTO);
    }

    @GetMapping("/equipment-faults/:id/read")
    @ApiOperation("批量设置设备异常已读" )
    public ResponseEntity<Void> batchReadEquipmentFault(@PathVariable @ApiParam(value = "设备异常主键列表", example = "123,234,567", required = true) List<Long> ids) {
        log.debug("REST request to batch update read status of EquipmentFault : {}", ids);
        equipmentFaultService.batchRead(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET  /equipment-faults} : get all the equipmentFaults.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of equipmentFaults in body.
     */
    @GetMapping("/equipment-faults")
    @ApiOperation("搜索设备异常记录")
    public ResponseEntity<PageData<EquipmentFaultDTO>> getAllEquipmentFaults(Pageable pageable, EquipmentFaultVM vm) {
        log.debug("REST request to get a page of EquipmentFaults");
        Page<EquipmentFaultDTO> page = equipmentFaultService.findAll(pageable, vm);
        return ResponseUtil.toPageData(page);
    }

    /**
     * {@code GET  /equipment-faults/:id} : get the "id" equipmentFault.
     *
     * @param id the id of the equipmentFaultDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the equipmentFaultDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/equipment-faults/{id}")
    @ApiOperation("获取设备异常详情")
    public ResponseEntity<EquipmentFaultDTO> getEquipmentFault(@PathVariable Long id) {
        log.debug("REST request to get EquipmentFault : {}", id);
        Optional<EquipmentFaultDTO> equipmentFaultDTO = equipmentFaultService.findOne(id);
        return ResponseUtil.wrapOrNotFound(equipmentFaultDTO);
    }
//
//    /**
//     * {@code DELETE  /equipment-faults/:id} : delete the "id" equipmentFault.
//     *
//     * @param id the id of the equipmentFaultDTO to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/equipment-faults/{id}")
//    public ResponseEntity<Void> deleteEquipmentFault(@PathVariable Long id) {
//        log.debug("REST request to delete EquipmentFault : {}", id);
//        equipmentFaultService.delete(id);
//        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
//    }
}
