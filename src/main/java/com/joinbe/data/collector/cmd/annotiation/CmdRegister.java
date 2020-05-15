package com.joinbe.data.collector.cmd.annotiation;

import com.joinbe.data.collector.netty.protocol.code.EventEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface CmdRegister {
    EventEnum event();
}
