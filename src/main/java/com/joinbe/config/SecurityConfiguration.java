package com.joinbe.config;

import com.joinbe.security.AuthoritiesConstants;
import com.joinbe.security.CustomAuthenticationConfigurer;
import com.joinbe.security.CustomAuthenticationProvider;
import com.joinbe.security.RedissonTokenStore;
import com.joinbe.security.jwt.JWTConfigurer;
import com.joinbe.security.jwt.TokenProvider;
import com.joinbe.security.permission.CustomAccessDecisionManager;
import com.joinbe.security.permission.CustomSecurityMetadataSource;
import com.joinbe.security.permission.PermissionFilterSecurityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;

    private final RedissonTokenStore tokenStore;

    private final CorsFilter corsFilter;

    private final CustomSecurityMetadataSource securityMetadataSource;

    private final SecurityProblemSupport problemSupport;

    private final UserDetailsService userDetailsService;

    private final CustomAuthenticationProvider provider;


    public SecurityConfiguration(TokenProvider tokenProvider, RedissonTokenStore tokenStore, CorsFilter corsFilter,
                                 SecurityProblemSupport problemSupport, CustomSecurityMetadataSource securityMetadataSource,
                                 UserDetailsService userDetailsService, CustomAuthenticationProvider provider) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.problemSupport = problemSupport;
        this.tokenStore = tokenStore;
        this.securityMetadataSource = securityMetadataSource;
        this.userDetailsService = userDetailsService;

        this.provider = provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/index.html")
            .antMatchers("/test/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf()
            .disable()
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(problemSupport)
            .accessDeniedHandler(problemSupport)
            .and()
            .headers()
            .contentSecurityPolicy("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
            .and()
            .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
            .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
            .and()
            .frameOptions()
            .deny()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/logout").permitAll()
            .antMatchers("/api/account/register").permitAll()
            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/account/activate").permitAll()
            .antMatchers("/api/account/reset-password/init").permitAll()
            .antMatchers("/api/account/reset-password/finish").permitAll()
            .antMatchers("/api/account/forgot-password/*/**").permitAll()
            .antMatchers("/api/account/forgot-password/finish").permitAll()
            .antMatchers("/api/**").authenticated()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/info").permitAll()
            .antMatchers("/management/prometheus").permitAll()
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .and()
            .httpBasic()
            .and()
            .addFilterBefore(new PermissionFilterSecurityInterceptor(securityMetadataSource, new CustomAccessDecisionManager()),
                FilterSecurityInterceptor.class)
            .apply(securityConfigurerAdapter());
        // @formatter:on
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider, tokenStore);
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Bean
    CustomAuthenticationConfigurer<AuthenticationManagerBuilder, UserDetailsService> customoAuthenticationConfigurer() {
        return new CustomAuthenticationConfigurer<>(userDetailsService, provider, passwordEncoder());
    }
}
