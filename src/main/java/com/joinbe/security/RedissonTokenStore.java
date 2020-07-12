package com.joinbe.security;

import io.github.jhipster.config.JHipsterProperties;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonTokenStore {

    private final Logger log = LoggerFactory.getLogger(RedissonTokenStore.class);

    private static final String TOKEN_KEY = "TOKEN_KEY";

    private static final String TOKEN_KEY_PREFIX = "USER_TOKEN:";

    private static final String DIVISION_KEY="DIVISION_KEY";

    private final RedissonClient redissonClient;

    private final JHipsterProperties jHipsterProperties;

    private long tokenValidityInSeconds;


    public RedissonTokenStore(RedissonClient redissonClient, JHipsterProperties jHipsterProperties) {
        this.redissonClient = redissonClient;
        this.jHipsterProperties = jHipsterProperties;

    }

    @PostConstruct
    public void init() {
        tokenValidityInSeconds = jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
    }

    public void saveLoginInfo(String login, UserLoginInfo loginInfo) {
        RMapCache<String, UserLoginInfo> tokenMap = redissonClient.getMapCache(TOKEN_KEY);
        tokenMap.put(login, loginInfo, tokenValidityInSeconds, TimeUnit.SECONDS);
    }

    public void removeLoginInfo(String login) {
        RMapCache<String, UserLoginInfo> tokenMap = redissonClient.getMapCache(TOKEN_KEY);
        tokenMap.remove(login);
    }

    public UserLoginInfo getUserLoginInfo(String login) {
        RMapCache<String, UserLoginInfo> tokenMap = redissonClient.getMapCache(TOKEN_KEY);
        return tokenMap.get(login);
    }

    public List<String> getUserDivisionIds(String login) {
        RMapCache<String, UserLoginInfo> tokenMap = redissonClient.getMapCache(TOKEN_KEY);
        UserLoginInfo token = tokenMap.get(login);
        if (token != null) {
            return token.getDivisionIds();
        }
        return new ArrayList<>();
    }


    public boolean isTokenExisted(String login) {
        RMapCache<String, UserLoginInfo> tokenMap = redissonClient.getMapCache(TOKEN_KEY);
        UserLoginInfo token = tokenMap.get(login);
        log.debug("Token in redis for user {}:{}", login, token);
        return token != null;
    }


}
