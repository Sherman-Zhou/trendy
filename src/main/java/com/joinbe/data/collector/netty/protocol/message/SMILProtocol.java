package com.joinbe.data.collector.netty.protocol.message;

import cn.hutool.core.util.ReflectUtil;
import com.joinbe.data.collector.netty.protocol.annotation.DataOrder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

public class SMILProtocol extends ProtocolMessage{

    public SMILProtocol(String data) {
        super(data);
    }

    @DataOrder(order = 0)
    private String ok;

    @DataOrder(order = 1)
    private String type;

    @DataOrder(order = 2)
    private String mileageMode;

    @DataOrder(order = 3)
    private BigDecimal mileageOffset;

    @DataOrder(order = 4)
    private BigDecimal mileageMultiple;

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
        } else if (BigDecimal.class.equals(field.getType())){
            ReflectUtil.invoke(this, methodName, new BigDecimal(data));
        }
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMileageMode() {
        return mileageMode;
    }

    public void setMileageMode(String mileageMode) {
        this.mileageMode = mileageMode;
    }

    public BigDecimal getMileageOffset() {
        return mileageOffset;
    }

    public void setMileageOffset(BigDecimal mileageOffset) {
        this.mileageOffset = mileageOffset;
    }

    public BigDecimal getMileageMultiple() {
        return mileageMultiple;
    }

    public void setMileageMultiple(BigDecimal mileageMultiple) {
        this.mileageMultiple = mileageMultiple;
    }
}
