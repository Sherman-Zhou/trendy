package com.joinbe.data.collector.netty.protocol;
import com.joinbe.data.collector.netty.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class StringProtocolInitalizer extends ChannelInitializer<SocketChannel> {

    @Autowired
    ServerHandler serverHandler;

    @Autowired
    private StringEncoder encoder;

    @Autowired
    private StringDecoder decoder;

    /**
     * tcp连接超时时间
     */
    private final static Integer READ_TIME_OUT_SEC = 5 * 60;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //截获管道
        ChannelPipeline pipeline = ch.pipeline();
        //设置数据decode处理器，for receiver
        pipeline.addLast(new PositionMessageDecoder());
        //设置编码处理器, for send
        pipeline.addLast(encoder);
        //设置数据处理器
        pipeline.addLast(serverHandler);
    }
}

