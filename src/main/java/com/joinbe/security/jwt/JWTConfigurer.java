package com.joinbe.security.jwt;

import com.joinbe.security.RedissonTokenStore;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;

    private final RedissonTokenStore tokenStore;

    public JWTConfigurer(TokenProvider tokenProvider, RedissonTokenStore tokenStore) {
        this.tokenProvider = tokenProvider;
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(HttpSecurity http) {
        JWTFilter customFilter = new JWTFilter(tokenProvider, tokenStore);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
