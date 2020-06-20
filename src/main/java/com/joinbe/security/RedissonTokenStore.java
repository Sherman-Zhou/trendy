package com.joinbe.security;

import io.github.jhipster.config.JHipsterProperties;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

    public void putInRedis(String login, String token) {
        RMapCache<String, String> tokenMap = redissonClient.getMapCache(TOKEN_KEY);
        tokenMap.put(TOKEN_KEY_PREFIX + login, token, tokenValidityInSeconds, TimeUnit.SECONDS);
    }

    public void removeFromRedis(String login) {
        RMapCache<String, String> tokenMap = redissonClient.getMapCache(TOKEN_KEY);
        tokenMap.remove(TOKEN_KEY_PREFIX + login);
    }

    public void storeUserDivision(String login, List<Long> divisionIds) {
        RMapCache<String, List<Long>> divisionMap =  redissonClient.getMapCache(DIVISION_KEY);
        divisionMap.put(login, divisionIds);
    }

    public List<Long> getUserDivisionIds(String login) {
        RMapCache<String, List<Long>> divisionMap =  redissonClient.getMapCache(DIVISION_KEY);
        return divisionMap.get(login);
    }

    public boolean isTokenExisted(String login) {
        RMapCache<String, String> tokenMap = redissonClient.getMapCache(TOKEN_KEY);
        String token = tokenMap.get(TOKEN_KEY_PREFIX + login);
        log.debug("Token in redis for user {}:{}", login, token);
        return StringUtils.isNotEmpty(token);
    }


}
