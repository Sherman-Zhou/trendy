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
         * ???????????????????????????????????????
         */
        if(numOfDevice.size() <= 1 ){
            //???????????????????????????
            deviceIdAndChannelMap.remove(deviceNo);
            //??????device?????????server
            redissonEquipmentStore.removeFromRedisForServer(deviceNo);
            //?????????????????????????????????
            redissonEquipmentStore.putInRedisForStatus(deviceNo,VehicleStatusEnum.UNKNOWN);
            //Ibutton?????????????????????
            redissonEquipmentStore.putInRedisForIButtonStatus(deviceNo, IbuttonStatusEnum.UNKNOWN);
            //???????????????????????????
            redissonEquipmentStore.putInRedisForDoorStatus(deviceNo, VehicleDoorStatusEnum.UNKNOWN);
            //??????/??????????????????????????? & ?????????????????????
            VehicleFireStatusEnum currentFireStatus = redissonEquipmentStore.getDeviceFireStatus(deviceNo);
            if(!VehicleFireStatusEnum.UNKNOWN.equals(currentFireStatus)){
                redissonEquipmentStore.putInRedisForPreFireStatus(deviceNo,currentFireStatus);
            }
            redissonEquipmentStore.putInRedisForFireStatus(deviceNo, VehicleFireStatusEnum.UNKNOWN);
            //???????????????????????????????????????????????????????????????
            new Thread(new Runnable() {
                @Override
                public void run() {
                    log.info("ChannelUnregistered, Device is offline, set status to offline, deviceNo: {}", deviceNo);
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
        log.debug("Received the client's message, Ip: {}, Id:{}, message???{}", channel.remoteAddress(), channel.id().asLongText(),JSONUtil.toJsonStr(msg));

        //Position message
        String deviceNo;
        if(msg instanceof PositionProtocol){
            deviceNo = ((PositionProtocol)msg).getUnitId();
            if(!channelIdAndDeviceIdMap.containsKey(channel.id().asLongText())){
                log.info("Client is connected???Ip: {}, Id:{}???deviceNo:{}", channel.remoteAddress(), channel.id().asLongText(),deviceNo);
                channelIdAndDeviceIdMap.put(channel.id().asLongText(), deviceNo);
                deviceIdAndChannelMap.put(deviceNo, channel);
                redissonEquipmentStore.putInRedisForServer(deviceNo,serverIp);
                //??????????????????????????????
                channel.eventLoop().execute(new Runnable() {
                    @Override
                    public void run() {
                        log.info("Get device's connection, set status to online, deviceNo: {}", deviceNo);
                        dataCollectService.updateStatus(deviceNo, true);
                    }
                });
            }
            //handle event
        }else{
            deviceNo = channelIdAndDeviceIdMap.get(channel.id().asLongText());
            log.debug("query result, device:data {}:{}", deviceNo, msg.getData());
            if(StringUtils.isBlank(deviceNo)){
                log.warn("Device not yet bound channel, channelId: {}", channel.id().asLongText());
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
                        }else if(deferredResult == null){
                            CommonProtocol commonProtocol = new CommonProtocol(bleNameProtocol.getData());
                            commonProtocol.setImei(deviceNo);
                            DeferredResult<ResponseEntity<ResponseDTO>> commonDeferredResult = LocalEquipmentStroe.get(deviceNo,EventEnum.C_CMD);
                            commonDeferredResult.setResult(new ResponseEntity<>(new CommonResponseDTO(0, "Device's response message", commonProtocol), HttpStatus.OK));
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
                        }else if(deferredResult == null){
                            CommonProtocol commonProtocol = new CommonProtocol(smilProtocol.getData());
                            commonProtocol.setImei(deviceNo);
                            DeferredResult<ResponseEntity<ResponseDTO>> commonDeferredResult = LocalEquipmentStroe.get(deviceNo,EventEnum.C_CMD);
                            commonDeferredResult.setResult(new ResponseEntity<>(new CommonResponseDTO(0, "Device's response message", commonProtocol), HttpStatus.OK));
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
        log.info("Client channel is active, clientIp???{}" ,clientIp);
        //send first command.
        log.debug("Send initial message???{}", this.factory.createInstance(EventEnum.GPOS.getEvent()).initCmd(new HashMap<>()));
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
        log.info("Client channel is inactive, Ip: {}, Id:{} ", channel.remoteAddress(), channel.id().asLongText());
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
        channelGroup.writeAndFlush("????????? - " + channel.remoteAddress() + "??????\n");
        channelGroup.add(channel);
        log.debug("??????????????????????????????, Ip: {}, Id:{} ", channel.remoteAddress(), channel.id().asLongText());
        log.debug("??????????????????{}", channelGroup.size());
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
        log.info("Client is offline already, Ip: {}, Id:{} ", channel.remoteAddress(), channel.id().asLongText());
        channelGroup.writeAndFlush("????????? - " + channel.remoteAddress() + "??????\n");
        // ??????(netty??????????????????????????????)
        channelGroup.remove(channel);
        log.info("Total number of clients???{}", channelGroup.size());
        super.handlerRemoved(ctx);
    }
    /**
     * triggered while exception is happened.
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // ??????????????????????????????
        Channel channel = ctx.channel();
        log.error("Client error :{}???try to restart the connection, Ip: {}, Id:{}",cause.getMessage(),channel.remoteAddress(), channel.id().asLongText());
        ctx.close();
    }
    /**
     * Interface call??? to send location message
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
        /*for (Map.Entry<String, Channel> entry : deviceIdAndChannelMap.entrySet()) {
            log.debug("deviceIdAndChannelMap Key: {}; value:{} ", entry.getKey() ,entry.getValue().id().asLongText());
        }
        for (Map.Entry<String, String> entry : channelIdAndDeviceIdMap.entrySet()) {
            log.debug("channelIdAndDeviceIdMap Key: {}; value:{} ", entry.getKey() ,entry.getValue());
        }*/
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
     * Interface call??? to send message
     * @param deviceId
     * @param event
     */
    public String sendMessage(String deviceId, String event) {
        Channel c = deviceIdAndChannelMap.get(deviceId);
        if (c == null) {
            String strInfo= "?????????????????????, deviceId: " + deviceId;
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
                log.debug("??????????????????{}", channelGroup.size());
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
