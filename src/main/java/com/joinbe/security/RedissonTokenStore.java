package com.joinbe.security;

import io.github.jhipster.config.JHipsterProperties;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.event.EntryExpiredListener;
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
        RMapCache<String, UserLoginInfo> tokenMap = redissonClient.getMapCache(TOKEN_KEY);
        tokenMap.addListener((EntryExpiredListener<String, UserLoginInfo>) event -> log.debug(" the user {} token is expired....", event.getKey(), event.getValue()));
    }

    @PostConstruct
    public void init() {
        tokenValidityInSeconds = jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
        log.debug("the expired time for redisson:{}secs", tokenValidityInSeconds);
    }

    public void saveLoginInfo(String login, UserLoginInfo loginInfo) {
        log.debug("start to save use info in redisson: {}", login);
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
       // log.debug("Token in redis for user {}:{}", login, token);
        return token != null;
    }


}
