package com.joinbe.data.collector.netty.protocol.annotation;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DataOrder {
    int order();
}
