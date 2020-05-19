package com.joinbe.data.collector.netty.handler;

import cn.hutool.json.JSONUtil;
import com.joinbe.data.collector.cmd.factory.CmdRegisterFactory;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.data.collector.netty.protocol.PositionProtocol;
import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * handlerAdded -> channelRegistered -> channelActive -> read
 * channelInactive -> channelUnregistered -> handlerRemoved
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<PositionProtocol> {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static HashMap<String, Channel> channelMap = new HashMap<>();
    private String locationCommand;

    @Autowired
    CmdRegisterFactory factory;

    @PostConstruct
    public void initSendPos(){
        Cmd cmd = factory.createInstance(EventEnum.GPOS.getEvent());
        if(cmd != null){
            this.locationCommand = cmd.initCmd(new HashMap<>());
        }
    }

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
        super.channelUnregistered(ctx);
    }

    /**
     * triggered while have message in channel
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PositionProtocol msg) {
        Channel channel = ctx.channel();
        log.debug("收到客户端消息,Ip: {}, Id:{}, 消息：{}", channel.remoteAddress(), channel.id().asLongText(),JSONUtil.toJsonStr(msg));
        String deviceNo = msg.getUnitId();
        Channel c = channelMap.get(deviceNo);
        if (c == null) {
            log.debug("客户端首次加入，Ip: {}, Id:{}，deviceNo:{}", channel.remoteAddress(), channel.id().asLongText(),deviceNo);
            channelMap.put(deviceNo, channel);
        } else if (c == channel) {
        } else {
            channelMap.put(deviceNo, channel);
            //关闭通道
            log.debug("通道发生变化，关闭原通道: Ip: {}, Id:{}", c.remoteAddress(),c.id().asLongText());
            c.close();
        }
        //save message
        channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                //insert msg, TODO
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
        log.debug("发送初始消息：{},{}", this.locationCommand,this.factory.createInstance(EventEnum.GPOS.getEvent()).initCmd(new HashMap<>()));
        ctx.channel().writeAndFlush(this.locationCommand);
        super.channelActive(ctx);
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
    public String sendMessage(String deviceId, String event, DeferredResult<Object> deferredResult) {
        Channel c = channelMap.get(deviceId);
        if (c == null) {
            String strInfo= "未找到发送通道, deviceId: " + deviceId;
            log.warn(strInfo);
            deferredResult.setErrorResult(strInfo);
            return strInfo;
        }
        log.debug("isActive:{}, isOpen:{}, isRegistered:{}, isWritable:{}",c.isActive(),c.isOpen(),c.isRegistered(),c.isWritable());
        c.writeAndFlush(event).addListener(future -> {
            if(future.isSuccess()){
                deferredResult.setResult("Success");
            }else{
                deferredResult.setResult("Equipment is not on line, deviceId: " + deviceId);
            }
        });
        return "success";
    }

    /**
     * Interface call， to send message
     * @param deviceId
     * @param event
     */
    public String sendMessage(String deviceId, String event) {
        Channel c = channelMap.get(deviceId);
        if (c == null) {
            String strInfo= "未找到发送通道, deviceId: " + deviceId;
            log.warn(strInfo);
            return strInfo;
        }
        log.debug("isActive:{}, isOpen:{}, isRegistered:{}, isWritable:{}",c.isActive(),c.isOpen(),c.isRegistered(),c.isWritable());
        c.writeAndFlush(event).addListener(future -> {
            if(future.isSuccess()){
                log.debug("send command success.");
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
