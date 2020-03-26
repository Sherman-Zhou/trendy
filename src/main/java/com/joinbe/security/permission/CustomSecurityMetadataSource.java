package com.joinbe.security.permission;


import com.joinbe.domain.Permission;
import com.joinbe.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;

import java.util.*;


@Component
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final Logger log = LoggerFactory.getLogger(CustomSecurityMetadataSource.class);

    private final PermissionService permissionService;

    private final PathMatcher pathMatcher;

    private Map<String, Collection<ConfigAttribute>> map = null;

    public CustomSecurityMetadataSource(PermissionService permissionService, PathMatcher pathMatcher) {
        this.permissionService = permissionService;
        this.pathMatcher = pathMatcher;
    }

    public void loadResourceDefine(){

        map = new HashMap<>(16);
        Collection<ConfigAttribute> configAttributes;
        ConfigAttribute cfg;
        // 获取启用的权限操作请求
        List<Permission> permissions = permissionService.loadUserPermissions();
        for(Permission permission : permissions) {
            if(StringUtils.isNotEmpty(permission.getTitle())&&StringUtils.isNotEmpty(permission.getBackendUrl())){
                configAttributes = new ArrayList<>();
                cfg = new SecurityConfig(permission.getTitle());

                configAttributes.add(cfg);

                map.put(permission.getBackendUrl(), configAttributes);
            }
        }
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {

        if(map == null){
            loadResourceDefine();
        }
        log.info("Http Method:{}", (((FilterInvocation) o).getHttpRequest().getMethod()));
        String url = ((FilterInvocation) o).getRequestUrl();
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String resURL = iterator.next();
            if (StringUtils.isNotEmpty(resURL)&& pathMatcher.match(resURL,url)) {
                log.info("getAttributes:{}-{}",resURL, map.get(resURL));
                return map.get(resURL);
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
