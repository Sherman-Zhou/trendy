package com.joinbe.common.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.joinbe.service.dto.UploadResultDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class EquipmentDataListener extends AnalysisEventListener<EquipmentData> {

    private final Logger log = LoggerFactory.getLogger(EquipmentDataListener.class);
    List<EquipmentData> list = new ArrayList<>();
    List<UploadResultDTO> errors = new ArrayList<>();
    @Autowired
    private MessageSource messageSource;

    /**
     * 在异常时会调改方法，抛出异常则停止读取, 如果这里不抛出异常则继续读取下一行
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Parse error:{}", exception.getMessage(), exception);
//        Integer rowIdx =  context.readRowHolder().getRowIndex();
//        String message = messageSource.getMessage("excel.upload.parse.error",
//            new String []  {exception.getMessage(), String.valueOf(rowIdx)}, LocaleContextHolder.getLocale());
        //recordError(message, rowIdx);
        throw new BadRequestAlertException(exception.getMessage(), "equipment.upload", "file.error");
    }

    /**
     * @param data
     * @param context
     */
    @Override
    public void invoke(EquipmentData data, AnalysisContext context) {
        //do material code validation
        Integer rowIdx = context.readRowHolder().getRowIndex();
        boolean hasError = false;
        if (StringUtils.isBlank(data.getIdentifyNumber())) {

            String message = messageSource.getMessage("equipment.upload.equipmentId.empty", new String[]{String.valueOf(rowIdx)}, LocaleContextHolder.getLocale());
            recordError(message, rowIdx);
            hasError = true;
        }
        if (StringUtils.isBlank(data.getImei())) {

            String message = messageSource.getMessage("equipment.upload.imei.empty", new String[]{String.valueOf(rowIdx)}, LocaleContextHolder.getLocale());
            recordError(message, rowIdx);
            hasError = true;
        }
        if (StringUtils.isBlank(data.getOrgName())) {

            String message = messageSource.getMessage("equipment.upload.division.empty", new String[]{String.valueOf(rowIdx)}, LocaleContextHolder.getLocale());
            recordError(message, rowIdx);
            hasError = true;
        }
        if (StringUtils.isBlank(data.getDivName())) {
            String message = messageSource.getMessage("equipment.upload.division.empty", new String[]{String.valueOf(rowIdx)}, LocaleContextHolder.getLocale());
            recordError(message, rowIdx);
            hasError = true;
        }

        data.setRowIdx(rowIdx);

        if (!hasError) {
            log.info("Parsed Data:{}", data);
            list.add(data);
        }
    }

    /**
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // save data
        log.info("completed all！");
    }

    /**
     * 表头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.debug("Parsed header:");
        headMap.entrySet().forEach((entry -> log.info("{}:{}", entry.getKey(), entry.getValue())));
        //do header validation
        if (headMap.size() != 7) {
            String message = messageSource.getMessage("excel.upload.wrong.template", null, LocaleContextHolder.getLocale());
            throw new RuntimeException(message);
        }
    }

    private void recordError(String message, int rowIdx) {
        UploadResultDTO result = new UploadResultDTO();
        result.setIsSuccess(false);
        result.setMsg(message);
        result.setRowNum(Long.valueOf(rowIdx));
        errors.add(result);
    }


    public List<EquipmentData> getList() {
        return list;
    }

    public void setList(List<EquipmentData> list) {
        this.list = list;
    }

    public List<UploadResultDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<UploadResultDTO> errors) {
        this.errors = errors;
    }
}