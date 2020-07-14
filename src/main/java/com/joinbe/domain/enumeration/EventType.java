package com.joinbe.domain.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    RELEASE("release"), //:发放密钥,
    REVOKE("revoke"),//: 收回密钥,
    LOCK("lock"),//:开锁,
    UNLOCK("unlock"),//:关锁,
    BINDING("binding"),//: 绑定设备,
    UNBINDING("unbinding"),//:解绑设备
    SETBLENAME("SETBLENAME");//:修改蓝牙名称

    private static final Map<String, EventType> mapping = new HashMap<>(6);

    @EnumValue
    private String code;

    static {
        EventType[] eventTypes = values();
        for (EventType eventType : eventTypes) {
            mapping.put(eventType.getCode(), eventType);
        }
    }

    EventType(String code) {
        this.code = code;
    }

    public static String getCode(EventType eventType) {
        return eventType != null ? eventType.getCode() : null;
    }

    public static EventType resolve(String code) {
        return code != null ? mapping.get(code) : null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
