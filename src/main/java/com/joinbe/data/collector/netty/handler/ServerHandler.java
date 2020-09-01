package com.joinbe.data.collector.netty.handler;

import cn.hutool.json.JSONUtil;
import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.data.collector.netty.protocol.message.*;
import com.joinbe.data.collector.service.DataCollectService;
import com.joinbe.data.collector.service.dto.*;
import com.joinbe.data.collector.store.LocalEquipmentStroe;
import com.joinbe.data.collector.store.RedissonEquipmentStore;
import com.joinbe.domain.enumeration.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * handlerAdded -> channelRegistered -> channelActive -> channelRead -> channelReadComplete
 * channelInactive -> channelUnregistered -> handlerRemoved
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<ProtocolMessage> {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //deviceId : channel
    private static final ConcurrentHashMap<String, Channel> deviceIdAndChannelMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> channelIdAndDeviceIdMap = new ConcurrentHashMap<>();

    @Value("${netty.server-url}")
    private String serverIp;

    @Autowired
    private RedissonEquipmentStore redissonEquipmentStore;

    @Autowired
    CmdRegisterFactory factory;

    @Autowired
    DataCollectService dataCollectService;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug("Channel registered, Ip: {}, Id:{}", channel.remoteAddress(), channel.id().asLongText());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug("Channel unregistered, Ip: {}, Id:{}", channel.remoteAddress(), channel.id().asLongText());
        String deviceNo = channelIdAndDeviceIdMap.get(channel.id().asLongText());

        List<String> numOfDevice = this.getKeyList(channelIdAndDeviceIdMap, deviceNo);
        /**
         * 防止设备断开重连时全部移除
         */
        if(numOfDevice.size() <= 1 ){
            //移除设备绑定的通道
            deviceIdAndChannelMap.remove(deviceNo);
            //移除device绑定的server
            redissonEquipmentStore.removeFromRedisForServer(deviceNo);
            //车辆行驶状态设置为未知
            redissonEquipmentStore.putInRedisForStatus(deviceNo,VehicleStatusEnum.UNKNOWN);
            //Ibutton状态设置为未知
            redissonEquipmentStore.putInRedisForIButtonStatus(deviceNo, IbuttonStatusEnum.UNKNOWN);
            //锁的状态设置为未知
            redissonEquipmentStore.putInRedisForDoorStatus(deviceNo, VehicleDoorStatusEnum.UNKNOWN);
            //开火/关火状态设置为未知 & 保存上次的状态
            VehicleFireStatusEnum currentFireStatus = redissonEquipmentStore.getDeviceFireStatus(deviceNo);
            if(!VehicleFireStatusEnum.UNKNOWN.equals(currentFireStatus)){
                redissonEquipmentStore.putInRedisForPreFireStatus(deviceNo,currentFireStatus);
            }
            redissonEquipmentStore.putInRedisForFireStatus(deviceNo, VehicleFireStatusEnum.UNKNOWN);
            //更新设备的状态为离线，车辆的行驶状态为未知
            new Thread(new Runnable() {
                @Override
                public void run() {
                    log.debug("ChannelUnregistered, Device is offline, set status to offline, deviceNo: {}", deviceNo);
                    dataCollectService.updateStatus(deviceNo, false, false);
                }
            }).start();
        }
        channelIdAndDeviceIdMap.remove(channel.id().asLongText());
        super.channelUnregistered(ctx);
    }

    /**
     * triggered while have message in channel
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage msg) {
        Channel channel = ctx.channel();
        log.debug("收到客户端消息,Ip: {}, Id:{}, 消息：{}", channel.remoteAddress(), channel.id().asLongText(),JSONUtil.toJsonStr(msg));

        //Position message
        String deviceNo;
        if(msg instanceof PositionProtocol){
            deviceNo = ((PositionProtocol)msg).getUnitId();
            if(!channelIdAndDeviceIdMap.containsKey(channel.id().asLongText())){
                log.debug("客户端首次加入，Ip: {}, Id:{}，deviceNo:{}", channel.remoteAddress(), channel.id().asLongText(),deviceNo);
                channelIdAndDeviceIdMap.put(channel.id().asLongText(), deviceNo);
                deviceIdAndChannelMap.put(deviceNo, channel);
                redissonEquipmentStore.putInRedisForServer(deviceNo,serverIp);
                //更新设备的状态为在线
                channel.eventLoop().execute(new Runnable() {
                    @Override
                    public void run() {
                        log.debug("Get device's connection, set status to online, deviceNo: {}", deviceNo);
                        dataCollectService.updateStatus(deviceNo, true);
                    }
                });
            }
            //handle event
        }else{
            deviceNo = channelIdAndDeviceIdMap.get(channel.id().asLongText());
            log.debug("query result, device:data {}:{}", deviceNo, msg.getData());
            if(StringUtils.isBlank(deviceNo)){
                log.warn("Device not yet bound channel");
                return;
            }
        }
        //save & handle query response message
        channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                if(msg instanceof PositionProtocol){
                    PositionProtocol positionProtocolMsg = (PositionProtocol)msg;
                    //DeferredResult deferredResult = redissonEquipmentStore.getInRedisForQuery(deviceNo, EventEnum.GPOS);
                    DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = LocalEquipmentStroe.get(deviceNo, EventEnum.GPOS);
                    if(deferredResult != null){
                        deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(0, "success", positionProtocolMsg), HttpStatus.OK));
                    }
                    dataCollectService.saveTrajectory(positionProtocolMsg);
                }else{
                    if(msg instanceof LockUnlockProtocol){
                        LockUnlockProtocol lockUnlockProtocol = (LockUnlockProtocol)msg;
                        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = LocalEquipmentStroe.get(deviceNo, EventEnum.SGPO);
                        if(deferredResult != null && "$OK".equals(lockUnlockProtocol.getOk())){
                            deferredResult.setResult(new ResponseEntity<>(new LockResponseDTO(0, "success", lockUnlockProtocol), HttpStatus.OK));
                        }else if (deferredResult != null && "$ERR".equals(lockUnlockProtocol.getOk())){
                            deferredResult.setResult(new ResponseEntity<>(new LockResponseDTO(1, "Get ERR response from device, device: " + deviceNo, lockUnlockProtocol), HttpStatus.OK));
                        }
                    }if(msg instanceof DLFWProtocol){
                        DLFWProtocol dlfwProtocol = (DLFWProtocol)msg;
                        DLFWeResponseItemDTO dlfWeResponseItemDTO = new DLFWeResponseItemDTO();
                        dlfWeResponseItemDTO.setData(dlfwProtocol.getData());
                        dlfWeResponseItemDTO.setImei(deviceNo);
                        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = LocalEquipmentStroe.get(deviceNo, EventEnum.DLFW);
                        deferredResult.setResult(new ResponseEntity<>(new DLFWResponseDTO(0, "Get response.", dlfWeResponseItemDTO), HttpStatus.OK));
                    }else if(msg instanceof BleNameProtocol){
                        BleNameProtocol bleNameProtocol = (BleNameProtocol)msg;
                        BleResponseItemDTO bleResponseItemDTO = new BleResponseItemDTO();
                        bleResponseItemDTO.setData(bleNameProtocol.getData());
                        bleResponseItemDTO.setOk(bleNameProtocol.getOk());
                        bleResponseItemDTO.setType(bleNameProtocol.getType());
                        bleResponseItemDTO.setBleName(bleNameProtocol.getBleName());
                        bleResponseItemDTO.setImei(deviceNo);
                        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = LocalEquipmentStroe.get(deviceNo, EventEnum.BLENAME);
                        if(deferredResult != null && "$OK".equals(bleNameProtocol.getOk())){
                            dataCollectService.insertEventLog(deviceNo, EventCategory.BLUETOOTH_NAME, EventType.SETBLENAME,OperationResult.SUCCESS);
                            dataCollectService.updateBleName(deviceNo, bleNameProtocol.getBleName());
                            deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(0, "success", bleResponseItemDTO), HttpStatus.OK));
                        }else if (deferredResult != null && "$ERR".equals(bleNameProtocol.getOk())){
                            dataCollectService.insertEventLog(deviceNo, EventCategory.BLUETOOTH_NAME, EventType.SETBLENAME,OperationResult.FAILURE);
                            deferredResult.setResult(new ResponseEntity<>(new BleResponseDTO(1, "Get ERR response from device, device: " + deviceNo, bleResponseItemDTO), HttpStatus.OK));
                        }
                    }else if(msg instanceof SMILProtocol){
                        SMILProtocol smilProtocol = (SMILProtocol)msg;
                        MileageResponseItemDTO mileageResponseItemDTO = new MileageResponseItemDTO();
                        mileageResponseItemDTO.setData(smilProtocol.getData());
                        mileageResponseItemDTO.setOk(smilProtocol.getOk());
                        mileageResponseItemDTO.setType(smilProtocol.getType());
                        mileageResponseItemDTO.setMileageMode(smilProtocol.getMileageMode());
                        mileageResponseItemDTO.setMileageOffset(smilProtocol.getMileageOffset());
                        mileageResponseItemDTO.setMileageMultiple(smilProtocol.getMileageMultiple());
                        mileageResponseItemDTO.setImei(deviceNo);
                        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = LocalEquipmentStroe.get(deviceNo, EventEnum.SMIL);
                        if(deferredResult != null && "$OK".equals(smilProtocol.getOk())){
                            dataCollectService.updateDeviceMileage(deviceNo, smilProtocol.getMileageOffset(),smilProtocol.getMileageMultiple());
                            deferredResult.setResult(new ResponseEntity<>(new MileageResponseDTO(0, "success", mileageResponseItemDTO), HttpStatus.OK));
                        }else if (deferredResult != null && "$ERR".equals(smilProtocol.getOk())){
                            deferredResult.setResult(new ResponseEntity<>(new MileageResponseDTO(1, "Get ERR response from device, device: " + deviceNo, mileageResponseItemDTO), HttpStatus.OK));
                        }
                    }else if(msg instanceof BMacProtocol){
                        BMacProtocol bMacProtocol = (BMacProtocol)msg;
                        BMacResponseItemDTO bMacResponseItemDTO = new BMacResponseItemDTO();
                        bMacResponseItemDTO.setData(bMacProtocol.getData());
                        //bMacResponseItemDTO.setMac(bMacProtocol.getMac());
                        bMacResponseItemDTO.setImei(deviceNo);
                        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = LocalEquipmentStroe.get(deviceNo, EventEnum.BMAC);
                        if(deferredResult != null && "$OK".equals(bMacProtocol.getOk())){
                            deferredResult.setResult(new ResponseEntity<>(new BMacResponseDTO(0, "success", bMacResponseItemDTO), HttpStatus.OK));
                        }else if (deferredResult != null && "$ERR".equals(bMacProtocol.getOk())){
                            deferredResult.setResult(new ResponseEntity<>(new BMacResponseDTO(1, "Get ERR response from device, device: " + deviceNo, bMacResponseItemDTO), HttpStatus.OK));
                        }
                    }else if(msg instanceof DoorProtocol){
                        DoorProtocol doorProtocol = (DoorProtocol)msg;
                        DoorResponseItemDTO doorResponseItemDTO = new DoorResponseItemDTO();
                        doorResponseItemDTO.setData(doorProtocol.getData());
                        doorResponseItemDTO.setOk(doorProtocol.getOk());
                        doorResponseItemDTO.setType(doorProtocol.getType());
                        doorResponseItemDTO.setMode(doorProtocol.getMode());
                        doorResponseItemDTO.setModeStatus(doorProtocol.getMode() == 1 ? "OPEN" : "CLOSE");
                        doorResponseItemDTO.setImei(deviceNo);
                        EventType eventType = doorProtocol.getMode() == 1 ? EventType.UNLOCK : EventType.LOCK;
                        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = LocalEquipmentStroe.get(deviceNo, EventEnum.DOOR);
                        if(deferredResult != null && "$OK".equals(doorProtocol.getOk())){
                            dataCollectService.insertEventLog(deviceNo, EventCategory.LOCK, eventType,OperationResult.SUCCESS);
                            deferredResult.setResult(new ResponseEntity<>(new DoorResponseDTO(0, "success", doorResponseItemDTO), HttpStatus.OK));
                        }else if (deferredResult != null && "$ERR".equals(doorProtocol.getOk())){
                            dataCollectService.insertEventLog(deviceNo, EventCategory.LOCK, eventType,OperationResult.FAILURE);
                            deferredResult.setResult(new ResponseEntity<>(new DoorResponseDTO(1, "Get ERR response from device, device: " + deviceNo, doorResponseItemDTO), HttpStatus.OK));
                        }
                    }else if(msg instanceof SetKeyProtocol){
                        SetKeyProtocol setKeyProtocol = (SetKeyProtocol)msg;
                        String expireDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().plusDays(7));
                        TokenResponseItem tokenResponseItem = new TokenResponseItem();
                        tokenResponseItem.setImei(deviceNo);
                        tokenResponseItem.setToken(setKeyProtocol.getDeviceToken());
                        tokenResponseItem.setExpireDate(expireDateTime);
                        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = LocalEquipmentStroe.get(deviceNo, EventEnum.SETKEY);
                        if(deferredResult != null && "$OK".equals(setKeyProtocol.getOk())){
                            dataCollectService.insertEventLog(deviceNo, OperationSourceType.APP,EventCategory.BLUETOOTH, EventType.RELEASE,OperationResult.SUCCESS);
                            deferredResult.setResult(new ResponseEntity<>(new TokenResponseDTO(0, "success", tokenResponseItem), HttpStatus.OK));
                        }else if(deferredResult != null && "$ERR".equals(setKeyProtocol.getOk())){
                            dataCollectService.insertEventLog(deviceNo, OperationSourceType.APP,EventCategory.BLUETOOTH, EventType.RELEASE,OperationResult.FAILURE);
                            deferredResult.setResult(new ResponseEntity<>(new TokenResponseDTO(1, "Get ERR response from device, device: " + deviceNo, tokenResponseItem), HttpStatus.OK));
                        }
                    }else if(msg instanceof CommonProtocol){
                        CommonProtocol commonProtocol = (CommonProtocol)msg;
                        commonProtocol.setImei(deviceNo);
                        DeferredResult<ResponseEntity<ResponseDTO>> deferredResult = LocalEquipmentStroe.get(deviceNo,EventEnum.C_CMD);
                        deferredResult.setResult(new ResponseEntity<>(new CommonResponseDTO(0, "Device's response message", commonProtocol), HttpStatus.OK));
                    }
                }
            }
        });
    }
    /**
     *  triggered while channel is ready
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        log.debug("客户端通道激活：clientIp：{}" ,clientIp);
        //send first command.
        log.debug("发送初始消息：{}", this.factory.createInstance(EventEnum.GPOS.getEvent()).initCmd(new HashMap<>()));
        ctx.channel().writeAndFlush(this.factory.createInstance(EventEnum.GPOS.getEvent()).initCmd(new HashMap<>()));
        super.channelActive(ctx);
    }
    /**
     * triggered while client is inactive
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug("客户端开始下线, Ip: {}, Id:{} ", channel.remoteAddress(), channel.id().asLongText());
        super.channelInactive(ctx);
    }
    /**
     * triggered while client is accepted
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("服务器 - " + channel.remoteAddress() + "加入\n");
        channelGroup.add(channel);
        log.debug("客户端连接初始化成功, Ip: {}, Id:{} ", channel.remoteAddress(), channel.id().asLongText());
        log.debug("客户端总数：{}", channelGroup.size());
        super.handlerAdded(ctx);
    }
    /**
     * triggered while client left
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug("客户端已经下线, Ip: {}, Id:{} ", channel.remoteAddress(), channel.id().asLongText());
        channelGroup.writeAndFlush("服务器 - " + channel.remoteAddress() + "离开\n");
        // 移出(netty会自动执行，不写也行)
        channelGroup.remove(channel);
        log.debug("客户端总数：{}", channelGroup.size());
        super.handlerRemoved(ctx);
    }
    /**
     * triggered while exception is happened.
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常就关闭连接
        Channel channel = ctx.channel();
        log.error("客户端异常:{}，关闭连接, Ip: {}, Id:{}",cause.getMessage(),channel.remoteAddress(), channel.id().asLongText());
        ctx.close();
    }
    /**
     * Interface call， to send location message
     * @param deviceId
     * @param event
     */
    public void sendLocationMessage(String deviceId, String event, DeferredResult<ResponseEntity<ResponseDTO>> deferredResult) {
        for (Map.Entry<String, Channel> entry : deviceIdAndChannelMap.entrySet()) {
            log.debug("deviceIdAndChannelMap Key: {}; value:{} ", entry.getKey() ,entry.getValue().id().asLongText());
        }
        for (Map.Entry<String, String> entry : channelIdAndDeviceIdMap.entrySet()) {
            log.debug("channelIdAndDeviceIdMap Key: {}; value:{} ", entry.getKey() ,entry.getValue());
        }
        Channel c = deviceIdAndChannelMap.get(deviceId);
        if (c == null) {
            String strInfo= "Equipment is offline, please try later,  device: " + deviceId;
            log.warn(strInfo);
            deferredResult.setErrorResult(new ResponseEntity<>(new LocationResponseDTO(1, strInfo), HttpStatus.OK));
            return;
        }
        log.debug("deviceId: {}, isActive:{}, isOpen:{}, isRegistered:{}, isWritable:{}",deviceId, c.isActive(),c.isOpen(),c.isRegistered(),c.isWritable());
        if(c.isWritable()){
            //put to queue
            boolean putResult = LocalEquipmentStroe.put(deviceId, EventEnum.GPOS, deferredResult);
            if(!putResult){
                deferredResult.setErrorResult(new ResponseEntity<>(new LocationResponseDTO(1, "Large concurrency, please try later: " + deviceId), HttpStatus.OK));
            }
            c.writeAndFlush(event).addListener(future -> {
                if(!future.isSuccess()){
                    deferredResult.setErrorResult(new ResponseEntity<>(new LocationResponseDTO(1, "Equipment is offline, device: " + deviceId), HttpStatus.OK));
                }else{
                    log.debug("sent command succeed, device: {}, command: {}", deviceId, event);
                }
            });
        }else{
            deferredResult.setErrorResult(new ResponseEntity<>(new LocationResponseDTO(1, "Equipment is offline and not writable, device: " + deviceId), HttpStatus.OK));
        }
    }

    /**
     *
     * @param deviceId
     * @param event
     * @param eventEnum
     * @param deferredResult
     */
    public void sendCommonQueryMessage(String deviceId, String event, EventEnum eventEnum, DeferredResult<ResponseEntity<ResponseDTO>> deferredResult) {
        for (Map.Entry<String, Channel> entry : deviceIdAndChannelMap.entrySet()) {
            log.debug("deviceIdAndChannelMap Key: {}; value:{} ", entry.getKey() ,entry.getValue().id().asLongText());
        }
        for (Map.Entry<String, String> entry : channelIdAndDeviceIdMap.entrySet()) {
            log.debug("channelIdAndDeviceIdMap Key: {}; value:{} ", entry.getKey() ,entry.getValue());
        }
        Channel c = deviceIdAndChannelMap.get(deviceId);
        if (c == null) {
            String strInfo= "Equipment is offline, Please try later, device: " + deviceId;
            log.warn(strInfo);
            deferredResult.setResult(new ResponseEntity<>(this.genCommonResponseByEvent(eventEnum,1, strInfo), HttpStatus.OK));
            return;
        }
        log.debug("deviceId: {}, isActive:{}, isOpen:{}, isRegistered:{}, isWritable:{}",deviceId, c.isActive(),c.isOpen(),c.isRegistered(),c.isWritable());
        if(c.isWritable()){
            boolean putResult = LocalEquipmentStroe.put(deviceId, eventEnum, deferredResult);
            if(!putResult){
                deferredResult.setResult(new ResponseEntity<>(this.genCommonResponseByEvent(eventEnum,1, "Large concurrency, please try later: " + deviceId), HttpStatus.OK));
            }
            c.writeAndFlush(event).addListener(future -> {
                if(!future.isSuccess()){
                    deferredResult.setResult(new ResponseEntity<>(this.genCommonResponseByEvent(eventEnum,1,"Equipment is offline, device: " + deviceId), HttpStatus.OK));
                }else{
                    log.debug("sent command succeed, deviceId: {}, command: {}", deviceId, event);
                }
            });
        }else{
            deferredResult.setResult(new ResponseEntity<>(this.genCommonResponseByEvent(eventEnum,1, "Equipment is offline and not writable, device: " + deviceId), HttpStatus.OK));
        }
    }

    /**
     *
     * @param eventEnum
     * @param code
     * @param message
     * @return
     */
    public ResponseDTO genCommonResponseByEvent(EventEnum eventEnum, int code, String message){
        switch (eventEnum.getEvent()) {
            case "SGPO":
                return new LockResponseDTO(code, message);
            case "DOOR":
                return new DoorResponseDTO(code, message);
            case "GPOS":
                return new LocationResponseDTO(code, message);
            case "SETKEY":
                return new TokenResponseDTO(code,message);
            case "BLENAME":
                return new BleResponseDTO(code, message);
            case "BMAC":
                return new BMacResponseDTO(code, message);
            case "SMIL":
                return new MileageResponseDTO(code, message);
            case "DLFW":
                return new DLFWResponseDTO(code, message);
            default:
                return new CommonResponseDTO(code, message);
        }
    }

    /**
     * Interface call， to send message
     * @param deviceId
     * @param event
     */
    public String sendMessage(String deviceId, String event) {
        Channel c = deviceIdAndChannelMap.get(deviceId);
        if (c == null) {
            String strInfo= "未找到发送通道, deviceId: " + deviceId;
            log.warn(strInfo);
            return strInfo;
        }
        log.debug("isActive:{}, isOpen:{}, isRegistered:{}, isWritable:{}",c.isActive(),c.isOpen(),c.isRegistered(),c.isWritable());
        c.writeAndFlush(event).addListener(future -> {
            if(future.isSuccess()){
                log.debug("send command success, deviceId:{}, command: {}", deviceId, event);
            }else{
                log.warn("send command failed : deviceId: {}, Ip: {}, Id:{} " + deviceId,c.remoteAddress(), c.id().asLongText());
                log.warn("disconnect device : deviceId: {}, Ip: {}, Id:{} " + deviceId,c.remoteAddress(), c.id().asLongText());
                c.close();
                log.debug("客户端总数：{}", channelGroup.size());
            }
        });
        return "success";
    }


    /**
     * get all keys by value
     * @param map
     * @param value
     * @return
     */
    public List<String> getKeyList(ConcurrentHashMap<String,String> map, String value){
        List<String> keyList = new ArrayList();
        for(String getKey: map.keySet()){
            if(map.get(getKey).equals(value)){
                keyList.add(getKey);
            }
        }
        return keyList;
    }
}
