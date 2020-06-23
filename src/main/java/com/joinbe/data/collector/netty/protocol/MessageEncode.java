package com.joinbe.data.collector.netty.protocol;

import com.joinbe.data.collector.netty.protocol.message.PositionProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncode extends MessageToByteEncoder<PositionProtocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          PositionProtocol msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.getData().getBytes());
    }
}
