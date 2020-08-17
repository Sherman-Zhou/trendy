package com.joinbe.data.collector.netty.protocol.message;

import cn.hutool.core.util.ReflectUtil;
import com.joinbe.data.collector.netty.protocol.annotation.DataOrder;

import java.lang.reflect.Field;
import java.util.List;

public class DLFWProtocol extends ProtocolMessage{

    public DLFWProtocol(String data) {
        super(data);
    }

    @Override
    public void processField(Field field, List<String> dataList) {
        return;
    }
}
