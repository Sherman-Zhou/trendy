package com.joinbe.common.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.joinbe.service.dto.RowParseError;
import com.joinbe.service.dto.UploadResponse;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final MessageSource messageSource;
    UploadResponse response = new UploadResponse();

    public EquipmentDataListener(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

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
        throw new BadRequestAlertException(exception.getMessage(), "equipment.upload", "parse.error");
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

            String message = messageSource.getMessage("error.equipment.equipmentId.empty", new String[]{String.valueOf(rowIdx)}, null, LocaleContextHolder.getLocale());
            recordError(message, rowIdx);
            hasError = true;
        }
        if (StringUtils.isBlank(data.getImei())) {

            String message = messageSource.getMessage("error.equipment.imei.empty", new String[]{String.valueOf(rowIdx)}, null, LocaleContextHolder.getLocale());
            recordError(message, rowIdx);
            hasError = true;
        }

        data.setRowIdx(rowIdx);
        response.increaseTotalRowsNum();
        if (!hasError) {
            log.info("Parsed Data:{}", data);
            response.increaseSuccessRowNum();
            list.add(data);
        } else {
            response.increaseFailedRowNum();
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
        if (headMap.size() != 6) {
            String message = messageSource.getMessage("", null, null, LocaleContextHolder.getLocale());
            throw new BadRequestAlertException(message, "equipment.upload", "wrong.template");
        }
    }

    private void recordError(String message, int rowIdx) {
        RowParseError result = new RowParseError();
        //result.setIsSuccess(false);
        result.setMsg(message);
        result.setRowNum((long) rowIdx);
        response.getErrors().add(result);
    }


    public List<EquipmentData> getList() {
        return list;
    }

    public void setList(List<EquipmentData> list) {
        this.list = list;
    }

    public UploadResponse getResponse() {
        return response;
    }

    public void setResponse(UploadResponse response) {
        this.response = response;
    }
}
