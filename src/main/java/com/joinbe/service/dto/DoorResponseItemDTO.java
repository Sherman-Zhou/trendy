package com.joinbe.service.dto;

import cn.hutool.core.util.ReflectUtil;
import com.joinbe.data.collector.netty.protocol.annotation.DataOrder;
import com.joinbe.data.collector.netty.protocol.message.ProtocolMessage;
import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.util.List;

public class DoorResponseItemDTO {

    private String data;

    private String ok;

    private String type;

    @ApiModelProperty(value = "锁的状态码, 1:打开状态; 0: 关闭状态")
    private Integer mode;

    @ApiModelProperty(value = "锁的状态, OPEN:打开状态; CLOSE: 关闭状态")
    private String modeStatus;

    @ApiModelProperty(value = "设备的imei")
    private String imei;

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

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getModeStatus() {
        return modeStatus;
    }

    public void setModeStatus(String modeStatus) {
        this.modeStatus = modeStatus;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
