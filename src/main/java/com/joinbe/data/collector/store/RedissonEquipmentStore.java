package com.joinbe.data.collector.store;

import cn.hutool.core.util.StrUtil;
import com.joinbe.config.Constants;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.domain.enumeration.*;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RMapCache;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonEquipmentStore {

    private final Logger log = LoggerFactory.getLogger(RedissonEquipmentStore.class);

    //主动查询相关
    private static final String DEVICE_QUERY_KEY = "DEVICE_ID|DEVICE_EVENT:";
    //设备服务器相关
    private static final String DEVICE_SERVER_KEY = "DEVICE_SERVER_KEY";
    private static final String DEVICE_SERVER_KEY_PREFIX = "DEVICE_ID_SERVER:";
    //实时状态相关
    private static final String DEVICE_REAL_TIME_STATUS_KEY = "DEVICE_ID_STATUS_KEY";
    private static final String DEVICE_REAL_TIME_STATUS_KEY_PREFIX = "DEVICE_ID_STATUS:";
    //实时状态计算相关
    private static final String DEVICE_REAL_TIME_CALC_STATUS_KEY = "DEVICE_ID_CALC_STATUS_KEY";
    private static final String DEVICE_REAL_TIME_CALC_STATUS_KEY_PREFIX = "DEVICE_ID_CALC_STATUS:";
    //轨迹相关
    private static final String DEVICE_TRAJECTORY_KEY = "DEVICE_TRAJECTORY_KEY";
    private static final String DEVICE_TRAJECTORY_KEY_PREFIX = "DEVICE_ID_CURRENT_TRAJECTORY:";
    //Ibutton状态相关
    private static final String DEVICE_REAL_TIME_IBUTTON_STATUS_KEY = "DEVICE_ID_IBUTTON_STATUS_KEY";
    private static final String DEVICE_REAL_TIME_IBUTTON_ID_KEY = "DEVICE_ID_IBUTTON_ID_KEY";
    private static final String DEVICE_REAL_TIME_IBUTTON_STATUS_KEY_PREFIX = "DEVICE_ID_IBUTTON_STATUS:";
    private static final String DEVICE_REAL_TIME_IBUTTON_ID_KEY_PREFIX = "DEVICE_ID_IBUTTON_ID:";
    //Door状态相关
    private static final String DEVICE_REAL_TIME_DOOR_STATUS_KEY = "DEVICE_ID_DOOR_STATUS_KEY";
    private static final String DEVICE_REAL_TIME_DOOR_STATUS_KEY_PREFIX = "DEVICE_ID_DOOR:";
    //打火熄火相关
    private static final String DEVICE_REAL_TIME_FIRE_STATUS_KEY = "DEVICE_ID_FIRE_STATUS_KEY";
    private static final String DEVICE_REAL_TIME_FIRE_STATUS_KEY_PREFIX = "DEVICE_ID_FIRE:";
    private static final String DEVICE_PRE_REAL_TIME_FIRE_STATUS_KEY = "DEVICE_ID_PRE_FIRE_STATUS_KEY";
    private static final String DEVICE_PRE_REAL_TIME_FIRE_STATUS_KEY_PREFIX = "DEVICE_ID_PRE_FIRE:";
    //讯号相关
    private static final String DEVICE_DOOR_SIGNAL_KEY = "DEVICE_DOOR_SIGNAL_KEY";
    private static final String DEVICE_DOOR_SIGNAL_KEY_PREFIX = "DEVICE_ID_DOOR_SIGNAL:";

    private final RedissonClient redissonClient;

    public RedissonEquipmentStore(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @PostConstruct
    public void init() {
        /*boolean result = LocalEquipmentStroe.put("111", EventEnum.GPOS, new DeferredResult<>(5000L, "Get Location time out"));
        log.debug("put result: {}", result);

        DeferredResult<ResponseEntity<ResponseDTO>> responseEntityDeferredResult = LocalEquipmentStroe.get("111", EventEnum.GPOS);
        log.debug("responseEntityDeferredResult: {}", responseEntityDeferredResult.toString());*/
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

    /**
     * save real-time status
     * @param deviceId
     * @param status: R - running;  S - stopped
     */
    public void putInRedisForStatus(String deviceId, VehicleStatusEnum status) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_STATUS_KEY);
        deviceServerMap.put(DEVICE_REAL_TIME_STATUS_KEY_PREFIX + deviceId, status.getCode());
    }

    /**
     * remove real time status
     * @param deviceId
     */
    public void removeFromRedisForStatus(String deviceId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_STATUS_KEY);
        deviceServerMap.remove(DEVICE_REAL_TIME_STATUS_KEY_PREFIX + deviceId);
    }

    /**
     * Get real time status
     * @param deviceId
     * @return
     */
    public VehicleStatusEnum getDeviceStatus(String deviceId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_STATUS_KEY);
        String status = deviceServerMap.get(DEVICE_REAL_TIME_STATUS_KEY_PREFIX + deviceId);
        log.debug("status in redis for deviceId {}:{}", deviceId, status);
        return VehicleStatusEnum.getByCode(status);
    }

    /**
     * save real ibutton status
     * @param deviceId
     * @param status: A - Attached;  R - Removed; U - Unkonwn
     */
    public void putInRedisForIButtonStatus(String deviceId, IbuttonStatusEnum status) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_IBUTTON_STATUS_KEY);
        deviceServerMap.put(DEVICE_REAL_TIME_IBUTTON_STATUS_KEY_PREFIX + deviceId, status.getCode());
    }

    /**
     * Ibutton Id
     * @param deviceId
     * @param iButtonId
     */
    public void putInRedisForIButtonId(String deviceId, String iButtonId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_IBUTTON_ID_KEY);
        deviceServerMap.put(DEVICE_REAL_TIME_IBUTTON_ID_KEY_PREFIX + deviceId, iButtonId);
    }

    /**
     * Get real ibutton status
     * @param deviceId
     * @return
     */
    public IbuttonStatusEnum getDeviceIButtonStatus(String deviceId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_IBUTTON_STATUS_KEY);
        String status = deviceServerMap.get(DEVICE_REAL_TIME_IBUTTON_STATUS_KEY_PREFIX + deviceId);
        log.debug("IButton Status in redis for deviceId {}:{}", deviceId, status);
        return IbuttonStatusEnum.getByCode(status);
    }

    /**
     * Get ibutton id
     * @param deviceId
     * @return
     */
    public String getDeviceIButtonId(String deviceId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_IBUTTON_ID_KEY);
        String iButtonId = deviceServerMap.get(DEVICE_REAL_TIME_IBUTTON_ID_KEY_PREFIX + deviceId);
        log.debug("IButtonId in redis for deviceId {}:{}", deviceId, iButtonId);
        return iButtonId;
    }


    /**
     * Record last time stopped time
     * @param deviceId
     * @param lastStopTime
     */
    public void putInRedisForCalcStatus(String deviceId, long lastStopTime) {
        RMapCache<String, Long> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_CALC_STATUS_KEY);
        deviceServerMap.put(DEVICE_REAL_TIME_CALC_STATUS_KEY_PREFIX + deviceId, lastStopTime);
    }

    /**
     * Remove last time stopped time
     * @param deviceId
     */
    public void removeFromRedisForCalcStatus(String deviceId) {
        RMapCache<String, Long> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_CALC_STATUS_KEY);
        deviceServerMap.remove(DEVICE_REAL_TIME_CALC_STATUS_KEY_PREFIX + deviceId);
    }

    /**
     * Get last time stopped time
     * @param deviceId
     * @return
     */
    public Long getDeviceCalcStatus(String deviceId) {
        RMapCache<String, Long> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_CALC_STATUS_KEY);
        Long lastStopTime = deviceServerMap.get(DEVICE_REAL_TIME_CALC_STATUS_KEY_PREFIX + deviceId);
        log.debug("lastRunningTime in redis for deviceId {}:{}", deviceId, lastStopTime);
        return lastStopTime;
    }


    /**
     * save trajectoryId
     * @param deviceId
     * @param trajectoryId
     */
    public void putInRedisForTrajectory(String deviceId, String trajectoryId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_TRAJECTORY_KEY);
        deviceServerMap.put(DEVICE_TRAJECTORY_KEY_PREFIX + deviceId, trajectoryId);
    }

    /**
     * Remove trajectoryId
     * @param deviceId
     */
    public void removeFromRedisForTrajectory(String deviceId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_TRAJECTORY_KEY);
        deviceServerMap.remove(DEVICE_TRAJECTORY_KEY_PREFIX + deviceId);
    }

    /**
     * Get trajectoryId
     * @param deviceId
     * @return
     */
    public String getDeviceTrajectory(String deviceId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_TRAJECTORY_KEY);
        String trajectoryId = deviceServerMap.get(DEVICE_TRAJECTORY_KEY_PREFIX + deviceId);
        log.debug("last trajectoryId in redis for deviceId {}:{}", deviceId, trajectoryId);
        return trajectoryId;
    }

    /**
     * save real-time status
     * @param deviceId
     * @param status: OPEN/CLOSE/UNKNOWN
     */
    public void putInRedisForDoorStatus(String deviceId, VehicleDoorStatusEnum status) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_DOOR_STATUS_KEY);
        deviceServerMap.put(DEVICE_REAL_TIME_DOOR_STATUS_KEY_PREFIX + deviceId, status.getCode());
    }

    /**
     * Get real time status
     * @param deviceId
     * @return
     */
    public VehicleDoorStatusEnum getDeviceDoorStatus(String deviceId) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_REAL_TIME_DOOR_STATUS_KEY);
        String status = deviceServerMap.get(DEVICE_REAL_TIME_DOOR_STATUS_KEY_PREFIX + deviceId);
        log.debug("Door status in redis for deviceId {}:{}", deviceId, status);
        return VehicleDoorStatusEnum.getByCode(status);
    }


    /**
     *
     * @param imei
     * @param sign
     */
    public void putInRedisForDoorSignal(String imei, DoorSignalEnum sign) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_DOOR_SIGNAL_KEY);
        deviceServerMap.put(DEVICE_DOOR_SIGNAL_KEY_PREFIX + imei, sign.getCode());
    }


    /**
     *
     * @param imei
     * @return
     */
    public DoorSignalEnum getDoorSignal(String imei) {
        RMapCache<String, String> deviceServerMap = redissonClient.getMapCache(DEVICE_DOOR_SIGNAL_KEY);
        String sign = deviceServerMap.get(DEVICE_DOOR_SIGNAL_KEY_PREFIX + imei);
        log.debug("Signal in redis for deviceId {}:{}", imei, sign);
        return DoorSignalEnum.getByCode(sign);
    }

    /**
     * save fire status
     * @param deviceId
     * @param status: OPEN_FIRE/CLOSE_FIRE/UNKNOWN
     */
    public void putInRedisForFireStatus(String deviceId, VehicleFireStatusEnum status) {
        RMapCache<String, String> deviceFireMap = redissonClient.getMapCache(DEVICE_REAL_TIME_FIRE_STATUS_KEY);
        deviceFireMap.put(DEVICE_REAL_TIME_FIRE_STATUS_KEY_PREFIX + deviceId, status.getCode());
    }

    /**
     * save pre fire status
     * @param deviceId
     * @param status: OPEN_FIRE/CLOSE_FIRE/UNKNOWN
     */
    public void putInRedisForPreFireStatus(String deviceId, VehicleFireStatusEnum status) {
        RMapCache<String, String> deviceFireMap = redissonClient.getMapCache(DEVICE_PRE_REAL_TIME_FIRE_STATUS_KEY);
        deviceFireMap.put(DEVICE_PRE_REAL_TIME_FIRE_STATUS_KEY_PREFIX + deviceId, status.getCode());
    }

    /**
     * Get fire status
     * @param deviceId
     * @return
     */
    public VehicleFireStatusEnum getDeviceFireStatus(String deviceId) {
        RMapCache<String, String> deviceFireMap = redissonClient.getMapCache(DEVICE_REAL_TIME_FIRE_STATUS_KEY);
        String status = deviceFireMap.get(DEVICE_REAL_TIME_FIRE_STATUS_KEY_PREFIX + deviceId);
        log.debug("Fire status in redis for deviceId {}:{}", deviceId, status);
        return VehicleFireStatusEnum.getByCode(status);
    }

    /**
     * Get pre fire status
     * @param deviceId
     * @return
     */
    public VehicleFireStatusEnum getDevicePreFireStatus(String deviceId) {
        RMapCache<String, String> deviceFireMap = redissonClient.getMapCache(DEVICE_PRE_REAL_TIME_FIRE_STATUS_KEY);
        String status = deviceFireMap.get(DEVICE_PRE_REAL_TIME_FIRE_STATUS_KEY_PREFIX + deviceId);
        log.debug("Pre fire status in redis for deviceId {}:{}", deviceId, status);
        return VehicleFireStatusEnum.getByCode(status);
    }

    /**
     * @return
     */
    public String genId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        Date date = new Date();
        String formatDate = sdf.format(date);
        String key = "key" + formatDate;
        Long incr = getIncr(key, getCurrent2TodayEndMillisTime());
        if (incr == 0) {
            incr = getIncr(key, getCurrent2TodayEndMillisTime());//从001开始
        }
        DecimalFormat df = new DecimalFormat("000000");//三位序列号
        return formatDate + df.format(incr);
    }

    /**
     * @param key
     * @param liveTime
     * @return
     */
    private Long getIncr(String key, long liveTime) {
        RAtomicLong entityIdCounter = redissonClient.getAtomicLong(key);
        Long increment = entityIdCounter.getAndIncrement();

        if ((null == increment || increment.longValue() == 0) && liveTime > 0) {//初始设置过期时间
            entityIdCounter.expire(liveTime, TimeUnit.MILLISECONDS);//单位毫秒
        }
        return increment;
    }

    /**
     * @return
     */
    private Long getCurrent2TodayEndMillisTime() {
        Calendar todayEnd = Calendar.getInstance();
        // Calendar.HOUR 12小时制, HOUR_OF_DAY 24小时制
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTimeInMillis() - System.currentTimeMillis();
    }
}
