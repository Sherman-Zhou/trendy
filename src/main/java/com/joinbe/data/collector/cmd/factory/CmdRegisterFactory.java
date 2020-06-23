package com.joinbe.data.collector.cmd.factory;

import com.joinbe.data.collector.cmd.annotiation.CmdRegister;
import com.joinbe.data.collector.cmd.register.Cmd;
import com.joinbe.service.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CmdRegisterFactory implements ApplicationListener<ContextRefreshedEvent> {
    protected final Logger log = LoggerFactory.getLogger(CmdRegisterFactory.class);
    private static Map<String, Cmd> REGISTER_NAME_MAP = new HashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 通过注解获取相关的类
        Map<String, Object> map = SpringContextUtils.getMapbeanwithAnnotion(CmdRegister.class);
        for (Map.Entry<String, Object> entryMap : map.entrySet()) {
            try {
                // 通过反射获取相关的实现类的Object
                Object object = SpringContextUtils.getTarget(entryMap.getValue());
                if (object != null) {
                    Cmd annotationService = (Cmd) object;
                    // 不为空的情况下，获取实现类的注解对象
                    // 并把注解对象的注解字段当做map的Key,实现类Object当做值
                    CmdRegister info = annotationService.getClass().getAnnotation(CmdRegister.class);
                    REGISTER_NAME_MAP.put(info.event().getEvent(), (Cmd) object);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 获取处理对象
     *
     * @param name
     * @return
     */
    public Cmd createInstance(String name) {
        Cmd reviewProcessor = REGISTER_NAME_MAP.get(name);
        if (reviewProcessor != null) {
            return reviewProcessor;
        } else {
            return null;
        }
    }

}
