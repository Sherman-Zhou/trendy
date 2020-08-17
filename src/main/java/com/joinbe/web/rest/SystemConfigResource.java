package com.joinbe.web.rest;

import com.joinbe.config.ApplicationProperties;
import com.joinbe.domain.SystemConfig;
import com.joinbe.domain.VehicleTrajectoryBackupFile;
import com.joinbe.security.SecurityUtils;
import com.joinbe.security.UserLoginInfo;
import com.joinbe.service.SystemConfigService;
import com.joinbe.service.VehicleTrajectoryService;
import com.joinbe.service.dto.SystemConfigDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

/**
 * REST controller for managing {@link SystemConfig}.
 */
@RestController
@RequestMapping("/api")
@Api(value = "系统配置相关接口", tags = {"系统配置相关接口"}, produces = "application/json")
public class SystemConfigResource {


    private final Logger log = LoggerFactory.getLogger(SystemConfigResource.class);

    private final SystemConfigService systemConfigService;

    private final VehicleTrajectoryService trajectoryService;

    private final MessageSource messageSource;

    private final ApplicationProperties properties;

    public SystemConfigResource(SystemConfigService systemConfigService, VehicleTrajectoryService trajectoryService,
                                MessageSource messageSource, ApplicationProperties properties) {
        this.systemConfigService = systemConfigService;
        this.trajectoryService = trajectoryService;
        this.messageSource = messageSource;
        this.properties = properties;
    }

    /**
     * {@code PUT  /configs} : Updates an existing systemConfig.
     *
     * @param systemConfigDTO the systemConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemConfigDTO,
     * or with status {@code 400 (Bad Request)} if the systemConfigDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the systemConfigDTO couldn't be updated.
     */
    @PutMapping("/configs")
    @ApiOperation("更新系统配置")
    public ResponseEntity<SystemConfigDTO> updateSystemConfig(@Valid @RequestBody SystemConfigDTO systemConfigDTO) {
        log.debug("REST request to update SystemConfig : {}", systemConfigDTO);
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        SystemConfigDTO result = systemConfigService.save(systemConfigDTO, loginInfo.getMerchantId());
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * {@code GET  /configs} : get  systemConfigs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of systemConfigs in body.
     */
    @GetMapping("/configs")
    @ApiOperation("获取系统配置")
    public SystemConfigDTO getSystemConfigs() {
        log.debug("REST request to get the SystemConfigs");
        SystemConfigDTO dto = systemConfigService.find();
        return dto;
    }

    @GetMapping("/trajectories/backup")
    @ApiOperation("设备轨迹备份")
    public ResponseEntity<Void> backupTrajectory() {

        log.debug("REST request to download TrajectoryReport!");
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        Locale locale = LocaleContextHolder.getLocale();
        trajectoryService.backupTrajectory(locale, loginInfo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trajectories/backup-files")
    @ApiOperation("获取所有轨迹备份文件")
    public List<VehicleTrajectoryBackupFile> getBackupFiles() {
        return systemConfigService.getAllBackupFiles();
    }

    @GetMapping("/trajectories/download/{id}")
    @ApiOperation("设备轨迹备份")
    public ResponseEntity<byte[]> downloadOrderStatsReport(@PathVariable @ApiParam(value = "备份文件id", required = true) Long id) throws Exception {
        VehicleTrajectoryBackupFile backupFile = systemConfigService.getBackFileById(id);
        byte[] fileContent = FileUtils.readFileToByteArray(new File(properties.getTrendy().getTrajectoryBackupFolder() + File.separator + backupFile.getRptName()));
        return download(fileContent, backupFile.getRptName());
    }

    private ResponseEntity<byte[]> download(byte[] content, String fileName) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Content-Disposition",
            "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        return new ResponseEntity<>(content, headers, HttpStatus.CREATED);
    }

}
