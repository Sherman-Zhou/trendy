package com.joinbe.data.collector.netty.protocol.message;

import cn.hutool.core.util.ReflectUtil;
import com.joinbe.data.collector.netty.protocol.annotation.DataOrder;

import java.lang.reflect.Field;
import java.util.List;

public class CommonProtocol extends ProtocolMessage{

    public CommonProtocol(String data) {
        super(data);
    }

    @Override
    public void processField(Field field, List<String> dataList) {
        DataOrder dataOrder = field.getAnnotation(DataOrder.class);
        if (dataOrder == null) {
            return;
        }
        String data = dataList.get(dataOrder.order());
        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        if (Integer.class.equals(field.getType())) {
            ReflectUtil.invoke(this, methodName, Integer.valueOf(data));
        } else if (Double.class.equals(field.getType())) {
            ReflectUtil.invoke(this, methodName, Double.valueOf(data));
        } else if (Float.class.equals(field.getType())) {
            ReflectUtil.invoke(this, methodName, Float.valueOf(data));
        } else if (String.class.equals(field.getType())) {
            ReflectUtil.invoke(this, methodName, data);
        }
    }
}
