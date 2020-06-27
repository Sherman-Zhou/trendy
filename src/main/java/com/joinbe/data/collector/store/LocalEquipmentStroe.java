package com.joinbe.data.collector.store;

import com.joinbe.config.Constants;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.data.collector.service.dto.ResponseDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalEquipmentStroe {

    private static final Logger log = LoggerFactory.getLogger(LocalEquipmentStroe.class);

    private static final String DEVICE_QUERY_KEY = "DEVICE_ID|DEVICE_EVENT:";
    private static ConcurrentHashMap<String, LinkedBlockingQueue<DeferredResult<ResponseEntity<ResponseDTO>>>> userDeferredResultMap = new ConcurrentHashMap<>();

    /**
     *
     * @param deviceId
     * @param event
     * @param deferredResult
     * @return
     */
    public synchronized static boolean put(String deviceId, EventEnum event , DeferredResult<ResponseEntity<ResponseDTO>> deferredResult) {
        log.debug("put in Local equipment stroe, deviceId: {}, event: {}", deviceId,event.getEvent());

        if (StringUtils.isBlank(deviceId) || Objects.isNull(event)) {
            return false;
        }
        String queryKey = new StringBuffer(DEVICE_QUERY_KEY)
            .append(deviceId)
            .append(Constants.C_VERTICAL_LINE)
            .append(event.getEvent()).toString();
        if(!userDeferredResultMap.containsKey(queryKey)){
            LinkedBlockingQueue<DeferredResult<ResponseEntity<ResponseDTO>>> gposQueue = new LinkedBlockingQueue<>();
            userDeferredResultMap.put(queryKey, gposQueue);
        }
        return userDeferredResultMap.get(queryKey).offer(deferredResult);
    }

    /**
     *
     * @param deviceId
     * @param event
     * @return
     */
    public static DeferredResult<ResponseEntity<ResponseDTO>>  get(String deviceId, EventEnum event) {
        log.debug("get from Local equipment stroe, deviceId: {}, event: {}", deviceId,event.getEvent());
        if (StringUtils.isBlank(deviceId) || Objects.isNull(event)) {
            return null;
        }
        String queryKey = new StringBuffer(DEVICE_QUERY_KEY)
            .append(deviceId)
            .append(Constants.C_VERTICAL_LINE)
            .append(event.getEvent()).toString();
        if (userDeferredResultMap.containsKey(queryKey)) {
            return userDeferredResultMap.get(queryKey).poll();
        }
        return null;
    }
}
