package com.joinbe.data.collector.redistore;

import com.joinbe.config.Constants;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;

@Component
public class RedissonEquipmentStore {

    private final Logger log = LoggerFactory.getLogger(RedissonEquipmentStore.class);

    private static final String DEVICE_QUERY_KEY = "DEVICE_ID|DEVICE_EVENT:";
    private static final String DEVICE_SERVER_KEY = "DEVICE_SERVER_KEY";
    private static final String DEVICE_SERVER_KEY_PREFIX = "DEVICE_ID:";

    private final RedissonClient redissonClient;

    public RedissonEquipmentStore(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @PostConstruct
    public void init() {

    }
    /**
     *
     * @param deviceId
     * @param event
     * @param deferredResult
     * @return
     */
    public boolean putInRedisForQuery(String deviceId, EventEnum event , DeferredResult deferredResult) {
        String queryKey = new StringBuffer(DEVICE_QUERY_KEY)
            .append(deviceId)
            .append(Constants.C_VERTICAL_LINE)
            .append(event.getEvent()).toString();
        RQueue<DeferredResult> queue = redissonClient.getQueue(queryKey);
        return queue.offer(deferredResult);
    }
    /**
     *
     * @param deviceId
     * @param event
     * @return
     */
    public DeferredResult getInRedisForQuery(String deviceId, EventEnum event) {
        String queryKey = new StringBuffer(DEVICE_QUERY_KEY)
            .append(deviceId)
            .append(Constants.C_VERTICAL_LINE)
            .append(event.getEvent()).toString();
        RQueue<DeferredResult> queue = redissonClient.getQueue(queryKey);
        return queue.poll();
    }

    /**
     *
     * @param deviceId
     * @param serverIp
     */
    public void putInRedisForServer(String deviceId, String serverIp) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_SERVER_KEY);
        deviceServerMap.put(DEVICE_SERVER_KEY_PREFIX + deviceId, serverIp);
    }

    /**
     *
     * @param deviceId
     */
    public void removeFromRedisForServer(String deviceId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_SERVER_KEY);
        deviceServerMap.remove(DEVICE_SERVER_KEY_PREFIX + deviceId);
    }

    /**
     *
     * @param deviceId
     * @return
     */
    public boolean isDeviceExisted(String deviceId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_SERVER_KEY);
        String serverIp = deviceServerMap.get(DEVICE_SERVER_KEY_PREFIX + deviceId);
        log.debug("serverIp in redis for deviceId {}:{}", serverIp, deviceId);
        return StringUtils.isNotEmpty(serverIp);
    }
}
