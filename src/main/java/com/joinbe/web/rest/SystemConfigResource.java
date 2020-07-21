package com.joinbe.web.rest;

import com.joinbe.common.util.DateUtils;
import com.joinbe.common.util.ExcelUtil;
import com.joinbe.domain.SystemConfig;
import com.joinbe.service.SystemConfigService;
import com.joinbe.service.VehicleTrajectoryService;
import com.joinbe.service.dto.SystemConfigDTO;
import com.joinbe.service.dto.TrajectoryReportDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    public SystemConfigResource(SystemConfigService systemConfigService, VehicleTrajectoryService trajectoryService, MessageSource messageSource) {
        this.systemConfigService = systemConfigService;
        this.trajectoryService = trajectoryService;
        this.messageSource = messageSource;
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
    @ApiOperation("获取系统配置")
    public SystemConfigDTO getSystemConfigs() {
        log.debug("REST request to get the SystemConfigs");
        SystemConfigDTO dto = systemConfigService.find();
        return dto;
    }

    @GetMapping("/trajectories/download")
    @ApiOperation("设备轨迹备份")
    public ResponseEntity<byte[]> downloadOrderStatsReport() throws Exception {
        final String TEMPLATE_FILE_NAME = "TrajectoryReport.xlsx";
        log.debug("REST request to download TrajectoryReport!");
        Locale locale = LocaleContextHolder.getLocale();

        SystemConfigDTO dto = systemConfigService.find();
        String yesterdayStr = DateUtils.formatDate(Instant.now().minus(1, ChronoUnit.DAYS), DateUtils.PATTERN_DATE);
        Instant yesterday = DateUtils.parseDate(yesterdayStr, DateUtils.PATTERN_DATE).toInstant();
        Instant from = DateUtils.parseDate(dto.getLastBackupTime(), DateUtils.PATTERN_DATE).toInstant().plus(1, ChronoUnit.DAYS);

        InputStream template = this.getClass().getResourceAsStream("/templates/excel/" + TEMPLATE_FILE_NAME);

        List<TrajectoryReportDTO> reportDTOS = trajectoryService.findAllTrajectory4backup(from, yesterday);
        Map<String, String> header = new HashMap<>();

        header.put("identifyNumberTitle", messageSource.getMessage("trajectory.excel.identifyNumberTitle", null, null, locale));
        header.put("imeiTitle", messageSource.getMessage("trajectory.excel.imeiTitle", null, null, locale));
        header.put("trajectoryIdTitle", messageSource.getMessage("trajectory.excel.trajectoryIdTitle", null, null, locale));
        header.put("receivedTimeTitle", messageSource.getMessage("trajectory.excel.receivedTimeTitle", null, null, locale));
        header.put("lngTitle", messageSource.getMessage("trajectory.excel.lngTitle", null, null, locale));
        header.put("latTitle", messageSource.getMessage("trajectory.excel.latTitle", null, null, locale));

        byte[] files = ExcelUtil.fillExcelReport(template, header, reportDTOS);

        dto.setLastBackupTime(yesterdayStr);
        systemConfigService.save(dto);
        String fileName = messageSource.getMessage("trajectory.excel.fileName", null, null, locale) + dto.getLastBackupTime() + "-" + yesterdayStr + ".xlsx";
        return download(files, fileName);
    }

    private ResponseEntity<byte[]> download(byte[] content, String fileName) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Content-Disposition",
            "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        return new ResponseEntity<>(content, headers, HttpStatus.CREATED);
    }


}
