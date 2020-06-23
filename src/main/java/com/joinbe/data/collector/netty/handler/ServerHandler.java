package com.joinbe.data.collector.netty.handler;

import cn.hutool.json.JSONUtil;
import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import com.joinbe.data.collector.netty.protocol.message.PositionProtocol;
import com.joinbe.data.collector.netty.protocol.message.ProtocolMessage;
import com.joinbe.data.collector.redistore.RedissonEquipmentStore;
import com.joinbe.data.collector.service.DataCollectService;
import com.joinbe.data.collector.service.dto.LocationResponseDTO;
import com.joinbe.data.collector.service.dto.ResponseDTO;
import com.joinbe.domain.enumeration.VehicleStatusEnum;
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
import java.util.HashMap;

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
    private static final HashMap<String, Channel> deviceIdAndChannelMap = new HashMap<>();
    private static final HashMap<String, String> channelIdAndDeviceIdMap = new HashMap<>();

    @Value("${netty.server-ip}")
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
        //移除设备绑定的通道
        deviceIdAndChannelMap.remove(deviceNo);
        channelIdAndDeviceIdMap.remove(channel.id().asLongText());
        //移除device绑定的server
        redissonEquipmentStore.removeFromRedisForServer(deviceNo);
        //车辆行驶状态设置为未知
        redissonEquipmentStore.putInRedisForStatus(deviceNo,VehicleStatusEnum.UNKNOWN);
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
            }
        }else{
            //TODO: Handle query results
            deviceNo = channelIdAndDeviceIdMap.get(channel.id().asLongText());
            if(StringUtils.isBlank(deviceNo)){
                log.warn("Device not yet bound channel");
                return;
            }
        }
        //save message
        channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                if(msg instanceof PositionProtocol){
                    PositionProtocol positionProtocolMsg = (PositionProtocol)msg;
                    DeferredResult deferredResult = redissonEquipmentStore.getInRedisForQuery(deviceNo, EventEnum.GPOS);
                    if(deferredResult != null){
                        deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(0, "success", positionProtocolMsg), HttpStatus.OK));
                    }
                    dataCollectService.saveTrajectory(positionProtocolMsg);
                }else{
                    //TODO : handle query message
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
     * Interface call， to send message
     * @param deviceId
     * @param event
     */
    public void sendLocationMessage(String deviceId, String event, DeferredResult<ResponseEntity<ResponseDTO>> deferredResult) {
        Channel c = deviceIdAndChannelMap.get(deviceId);
        if (c == null) {
            String strInfo= "未找到发送通道, deviceId: " + deviceId;
            log.warn(strInfo);
            deferredResult.setErrorResult(new ResponseEntity<>(new LocationResponseDTO(1, strInfo), HttpStatus.OK));
            return;
        }
        log.debug("deviceId: {}, isActive:{}, isOpen:{}, isRegistered:{}, isWritable:{}",deviceId, c.isActive(),c.isOpen(),c.isRegistered(),c.isWritable());
        if(c.isWritable()){
            //put to queue
            redissonEquipmentStore.putInRedisForQuery(deviceId,EventEnum.GPOS,deferredResult);
            c.writeAndFlush(event).addListener(future -> {
                if(!future.isSuccess()){
                    deferredResult.setErrorResult(new ResponseEntity<>(new LocationResponseDTO(1, "Equipment is offline, deviceId: " + deviceId), HttpStatus.OK));
                }else{
                    log.debug("sent command succeed, deviceId: {}, command: {}", deviceId, event);
                }
            });
        }else{
            deferredResult.setErrorResult(new ResponseEntity<>(new LocationResponseDTO(1, "Equipment is offline and not writable, deviceId: " + deviceId), HttpStatus.OK));
        }
    }

    public void sendMessage(String deviceId, String event, EventEnum eventEnum, DeferredResult<ResponseEntity<ResponseDTO>> deferredResult) {
        Channel c = deviceIdAndChannelMap.get(deviceId);
        if (c == null) {
            String strInfo= "未找到发送通道, deviceId: " + deviceId;
            log.warn(strInfo);
            deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(1, strInfo), HttpStatus.OK));
            return;
        }
        log.debug("deviceId: {}, isActive:{}, isOpen:{}, isRegistered:{}, isWritable:{}",deviceId, c.isActive(),c.isOpen(),c.isRegistered(),c.isWritable());
        if(c.isWritable()){
            //TODO : put to queue
            //redissonEquipmentStore.putInRedisForQuery(deviceId,eventEnum,deferredResult);
            c.writeAndFlush(event).addListener(future -> {
                if(!future.isSuccess()){
                    deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(1, "Equipment is offline, deviceId: " + deviceId), HttpStatus.OK));
                }else{
                    //TODO : 修改为通道返回
                    deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(0, "Success: " + deviceId), HttpStatus.OK));
                    log.debug("sent command succeed, deviceId: {}, command: {}", deviceId, event);
                }
            });
        }else{
            deferredResult.setResult(new ResponseEntity<>(new LocationResponseDTO(1, "Equipment is offline and not writable, deviceId: " + deviceId), HttpStatus.OK));
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
}
