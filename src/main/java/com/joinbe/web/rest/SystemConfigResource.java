package com.joinbe.web.rest;

import com.joinbe.domain.SystemConfig;
import com.joinbe.service.SystemConfigService;
import com.joinbe.service.dto.SystemConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;

/**
 * REST controller for managing {@link SystemConfig}.
 */
@RestController
@RequestMapping("/api")
public class SystemConfigResource {


    private final Logger log = LoggerFactory.getLogger(SystemConfigResource.class);
    private final SystemConfigService systemConfigService;


    public SystemConfigResource(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    /**
     * {@code PUT  /configs} : Updates an existing systemConfig.
     *
     * @param systemConfigDTO the systemConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemConfigDTO,
     * or with status {@code 400 (Bad Request)} if the systemConfigDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the systemConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/configs")
    public ResponseEntity<SystemConfigDTO> updateSystemConfig(@Valid @RequestBody SystemConfigDTO systemConfigDTO) {
        log.debug("REST request to update SystemConfig : {}", systemConfigDTO);
        SystemConfigDTO result = systemConfigService.save(systemConfigDTO);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * {@code GET  /configs} : get  systemConfigs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of systemConfigs in body.
     */
    @GetMapping("/configs")
    public SystemConfigDTO getSystemConfigs() {
        log.debug("REST request to get the SystemConfigs");
        SystemConfigDTO dto = systemConfigService.find();
        return dto;
    }
}
