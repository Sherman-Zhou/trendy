package com.joinbe.data.collector.netty.protocol;

import cn.hutool.core.util.StrUtil;
import com.joinbe.data.collector.netty.protocol.message.PositionProtocol;
import com.joinbe.data.collector.netty.protocol.message.ProtocolMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(MessageDecoder.class);

    /**
     * <pre>
     * 协议开始的标准head_data，int类型，占据1个字节.
     * 表示数据的长度contentLength，int类型，占据2个字节.
     * 加密状态 int类型，占1个字节
     * 校验位 int,占2个字节
     * 数据位 int,占1个字节
     * </pre>
     */

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, List<Object> out) throws Exception {
        ByteBuf frame = buffer.retainedDuplicate();
        String data = convertByteBufToString(frame);
        log.info("Orig data from equipment: {}", data);
        if (StrUtil.isEmpty(data)) {
            return;
        }
        ProtocolMessage message;
        if (data.startsWith("$OK")) {
            //TODO - handle query response
            return;
        }else{
            //Position data
            message = new PositionProtocol(data);
        }
        message.initData();
        out.add(message);
        buffer.skipBytes(buffer.readableBytes());
    }

    public String convertByteBufToString(ByteBuf buf) {
        String str;
        // 处理堆缓冲区
        if (buf.hasArray()) {
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else {
            // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }
}