package com.joinbe.data.collector.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("Client heartbeat not detected, remove the client: {}", ctx.channel().id().asLongText());
                ctx.disconnect();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}

