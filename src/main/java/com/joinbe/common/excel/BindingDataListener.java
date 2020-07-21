package com.joinbe.common.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.joinbe.service.dto.RowParseError;
import com.joinbe.service.dto.UploadResponse;
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
public class BindingDataListener extends AnalysisEventListener<BindingData> {
    private final Logger log = LoggerFactory.getLogger(BindingDataListener.class);
    List<BindingData> list = new ArrayList<>();
    private UploadResponse response = new UploadResponse();

    @Autowired
    private MessageSource messageSource;

    @Override
    public void invoke(BindingData data, AnalysisContext context) {
        Integer rowIdx = context.readRowHolder().getRowIndex();
        boolean hasError = false;

        if (StringUtils.isBlank(data.getLicensePlateNumber())) {
            String message = messageSource.getMessage("error.binding.vehicle.licensePlateNumber.empty", new String[]{String.valueOf(rowIdx)}, null, LocaleContextHolder.getLocale());
            recordError(message, rowIdx);
            hasError = true;
        }
        if (StringUtils.isBlank(data.getIdentifyNumber())) {

            String message = messageSource.getMessage("error.binding.vehicle.identifyNumber.empty", new String[]{String.valueOf(rowIdx)}, null, LocaleContextHolder.getLocale());
            recordError(message, rowIdx);
            hasError = true;
        }
        response.increaseTotalRowsNum();
        data.setRowIdx(rowIdx);

        if (hasError) {
            response.increaseFailedRowNum();
        } else {
            log.info("Parsed Data:{}", data);
            response.increaseSuccessRowNum();
            list.add(data);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

        log.info("所有数据解析完成！");
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
        if (headMap.size() != 2) {
            String message = messageSource.getMessage("excel.upload.wrong.template", null, null, LocaleContextHolder.getLocale());
            throw new RuntimeException(message);
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Parse error:{}", exception.getMessage(), exception);
//        Integer rowIdx =  context.readRowHolder().getRowIndex();
//        String message = messageSource.getMessage("excel.upload.parse.error",
//            new String []  {exception.getMessage(), String.valueOf(rowIdx)}, LocaleContextHolder.getLocale());
//          recordError(message, rowIdx);
        throw new BadRequestAlertException(exception.getMessage(), "binding.upload", "parse.error");
    }

    private void recordError(String message, int rowIdx) {
        RowParseError result = new RowParseError();
        // result.setIsSuccess(false);
        result.setMsg(message);
        result.setRowNum((long) rowIdx);
        response.addError(result);
    }

    public List<BindingData> getList() {
        return list;
    }

    public void setList(List<BindingData> list) {
        this.list = list;
    }

    public UploadResponse getResponse() {
        return response;
    }

    public void setResponse(UploadResponse response) {
        this.response = response;
    }
}
