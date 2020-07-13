package com.joinbe.data.collector.netty.protocol;

import cn.hutool.core.util.StrUtil;
import com.joinbe.data.collector.netty.protocol.message.*;
import com.joinbe.data.collector.service.dto.LocationResponseDTO;
import com.joinbe.data.collector.service.dto.LockResponseDTO;
import com.joinbe.data.collector.service.dto.ResponseDTO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.lang3.StringUtils;
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
        if (data.startsWith("$OK") || data.startsWith("$ERR")) {
            //TODO - handle query response
            String[] splitEventType = data.split(StrUtil.COMMA);
            String eventType = null;
            if(splitEventType.length >=2 && StringUtils.isNotEmpty(splitEventType[1])){
                eventType = splitEventType[1];
            }
            if(StringUtils.isEmpty(eventType)){
                return;
            }
            switch (eventType) {
                case "SGPO":
                    message = new LockUnlockProtocol(data);
                    message.initData(LockUnlockProtocol.class);
                    break;
                case "DOOR":
                    message = new DoorProtocol(data);
                    message.initData(DoorProtocol.class);
                    break;
                case "SETKEY":
                    message = new SetKeyProtocol(data);
                    message.initData(SetKeyProtocol.class);
                    break;
                case "BLENAME":
                    message = new BleNameProtocol(data);
                    message.initData(BleNameProtocol.class);
                    break;
                default:
                    message = new CommonProtocol(data);
                    message.initData(CommonProtocol.class);
            }
        }else{
            //Position data
            message = new PositionProtocol(data);
            message.initData(PositionProtocol.class);
        }
        out.add(message);
        buffer.skipBytes(buffer.readableBytes());
    }

    /**
     *
     * @param buf
     * @return
     */
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
        /**
         * remove \r\n
         */
        if(str.length() > 2){
            str = str.substring(0, str.length()-2);
        }
        return str;
    }
}
