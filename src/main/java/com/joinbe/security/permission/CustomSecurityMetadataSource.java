package com.joinbe.security.permission;


import com.joinbe.common.util.RequestKey;
import com.joinbe.domain.Permission;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Component
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final Logger log = LoggerFactory.getLogger(CustomSecurityMetadataSource.class);

    private final PermissionService permissionService;

    private final PathMatcher pathMatcher;

    private Map<RequestKey, Collection<ConfigAttribute>> map = null;

    public CustomSecurityMetadataSource(PermissionService permissionService, PathMatcher pathMatcher) {
        this.permissionService = permissionService;
        this.pathMatcher = pathMatcher;
    }


    @PostConstruct
    private void init() {
        this.loadResourceDefine();
    }

    public synchronized void loadResourceDefine() {

        map = new HashMap<>(16);
        Collection<ConfigAttribute> configAttributes;
        ConfigAttribute cfg;
        // 获取启用的权限操作请求
        List<Permission> permissions = permissionService.loadAllPermissions();
        for (Permission permission : permissions) {

            if (StringUtils.isNotEmpty(permission.getBackendUrl())) {

                if (CollectionUtils.isEmpty(permission.getChildren())
                    || permission.getChildren().stream().allMatch(child -> !RecordStatus.ACTIVE.equals(child.getStatus()))) {
                    configAttributes = new ArrayList<>();
                    cfg = new SecurityConfig(permission.getKey());

                    configAttributes.add(cfg);
                    String method = permission.getOperationType() != null ? permission.getOperationType().getMethod().toString() : null;
                    map.put(new RequestKey(permission.getBackendUrl(), method), configAttributes);
                }

            }

        }
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {

        if (map == null) {
            loadResourceDefine();
        }
        HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        String method = request.getMethod();
        String url = request.getRequestURI();
        Iterator<RequestKey> iterator = map.keySet().iterator();
        log.debug("url:method={}:{} ", url, method);
        while (iterator.hasNext()) {
            RequestKey requestKey = iterator.next();

            if (pathMatcher.match(requestKey.getUrl(), url)) {
                log.debug("url matched: {}", requestKey);
                if (requestKey.getMethod() != null) {
                    //at btn level
                    if (method.equals(requestKey.getMethod())) {
                        log.debug("matched attr: {}", map.get(requestKey));
                        return map.get(requestKey);
                    }
                } else {
                    //at menu level
                    log.debug("matched attr: {}", map.get(requestKey));
                    return map.get(requestKey);
                }
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
