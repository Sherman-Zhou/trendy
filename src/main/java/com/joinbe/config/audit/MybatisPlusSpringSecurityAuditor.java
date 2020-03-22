package com.joinbe.config.audit;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.joinbe.security.SpringSecurityAuditorAware;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MybatisPlusSpringSecurityAuditor implements MetaObjectHandler {

    private final SpringSecurityAuditorAware springSecurityAuditorAware;

    public MybatisPlusSpringSecurityAuditor(SpringSecurityAuditorAware springSecurityAuditorAware) {
        this.springSecurityAuditorAware = springSecurityAuditorAware;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createdBy", springSecurityAuditorAware.getCurrentAuditor().get(), metaObject);
        this.setFieldValByName("createdDate", Instant.now(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("lastModifiedBy", springSecurityAuditorAware.getCurrentAuditor().get(), metaObject);
        this.setFieldValByName("lastModifiedDate", Instant.now(), metaObject);
    }
}
