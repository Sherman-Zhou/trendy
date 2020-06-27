package com.joinbe.data.collector.netty.protocol.message;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Field;
import java.util.List;

public abstract class ProtocolMessage {

    public ProtocolMessage(String data) {
        this.data = data;
    }

    public ProtocolMessage(){}

    private String data;
    /**
     * 解析content内容
     */
    public void initData(Class<?> beanClass) {
        List<String> d = StrUtil.split(this.data, StrUtil.C_COMMA);
        Field[] fields = ReflectUtil.getFields(beanClass);
        List<Field> fieldList = CollUtil.newArrayList(fields);
        fieldList.forEach(field -> processField(field, d));
    }

    public abstract void processField(Field field, List<String> dataList);

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
