package com.joinbe.data.collector.netty.protocol;

import com.joinbe.data.collector.netty.handler.ServerHandler;
import com.joinbe.data.collector.netty.handler.ServerIdleStateTrigger;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StringProtocolInitalizer extends ChannelInitializer<SocketChannel> {

    @Autowired
    ServerHandler serverHandler;

    @Autowired
    private StringEncoder encoder;

    @Autowired
    private StringDecoder decoder;

    @Autowired
    private ServerIdleStateTrigger serverIdleStateTrigger;
    /**
     * tcp连接超时时间
     */
    @Value("${netty.idle-timeout-second}")
    private Integer readTimeOutSecond;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //截获管道
        ChannelPipeline pipeline = ch.pipeline();
        //心跳
        pipeline.addLast(new IdleStateHandler(readTimeOutSecond, 0, 0));
        ch.pipeline().addLast(serverIdleStateTrigger);
        //设置数据decode处理器，for receiver
        pipeline.addLast(new MessageDecoder());
        //设置编码处理器, for send
        pipeline.addLast(encoder);
        //设置数据处理器
        pipeline.addLast(serverHandler);
    }
}

