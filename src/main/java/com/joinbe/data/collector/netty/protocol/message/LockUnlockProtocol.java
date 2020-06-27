package com.joinbe.data.collector.netty.protocol.message;

import cn.hutool.core.util.ReflectUtil;
import com.joinbe.data.collector.netty.protocol.annotation.DataOrder;

import java.lang.reflect.Field;
import java.util.List;

public class LockUnlockProtocol extends ProtocolMessage{

    public LockUnlockProtocol(String data) {
        super(data);
    }

    @DataOrder(order = 0)
    private String ok;

    @DataOrder(order = 1)
    private String type;

    @DataOrder(order = 2)
    private Integer outputNum;

    @DataOrder(order = 3)
    private String outputStatus;

    @DataOrder(order = 4)
    private Integer OutpuptOnDuration;

    @DataOrder(order = 5)
    private Integer OutpuptOffDuration;

    @DataOrder(order = 6)
    private Integer OutputTimes;

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

    public Integer getOutputNum() {
        return outputNum;
    }

    public void setOutputNum(Integer outputNum) {
        this.outputNum = outputNum;
    }

    public String getOutputStatus() {
        return outputStatus;
    }

    public void setOutputStatus(String outputStatus) {
        this.outputStatus = outputStatus;
    }

    public Integer getOutpuptOnDuration() {
        return OutpuptOnDuration;
    }

    public void setOutpuptOnDuration(Integer outpuptOnDuration) {
        OutpuptOnDuration = outpuptOnDuration;
    }

    public Integer getOutpuptOffDuration() {
        return OutpuptOffDuration;
    }

    public void setOutpuptOffDuration(Integer outpuptOffDuration) {
        OutpuptOffDuration = outpuptOffDuration;
    }

    public Integer getOutputTimes() {
        return OutputTimes;
    }

    public void setOutputTimes(Integer outputTimes) {
        OutputTimes = outputTimes;
    }
}
