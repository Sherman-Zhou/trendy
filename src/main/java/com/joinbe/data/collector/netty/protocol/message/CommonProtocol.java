package com.joinbe.data.collector.netty.protocol.message;

import java.lang.reflect.Field;
import java.util.List;

public class CommonProtocol extends ProtocolMessage{

    private String imei;

    public CommonProtocol(String data) {
        super(data);
    }

    @Override
    public void processField(Field field, List<String> dataList) {
        return;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
