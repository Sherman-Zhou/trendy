package com.joinbe.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joinbe.domain.Staff;
import com.joinbe.security.RedissonTokenStore;
import com.joinbe.security.SecurityUtils;
import com.joinbe.security.jwt.JWTFilter;
import com.joinbe.security.jwt.TokenProvider;
import com.joinbe.service.UserService;
import com.joinbe.web.rest.vm.LoginVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
@Api(value ="用户登陆相关接口", tags={"用户登陆相关接口"}, produces = "application/json" )
public class UserJWTController {


    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final RedissonTokenStore redissonTokenStore;
    private final UserService userService;

    public UserJWTController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder,
                             RedissonTokenStore redissonTokenStore,
                             @Qualifier("JpaUserService") UserService userService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.redissonTokenStore = redissonTokenStore;

        this.userService = userService;
    }

    @PostMapping("/authenticate")
    @ApiOperation("用户登陆")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        Optional<Staff> userOptional;
        if (new EmailValidator().isValid(loginVM.getUsername(), null)) {
            userOptional = userService.findOneByEmailIgnoreCase(loginVM.getUsername());
            if (userOptional.isPresent()) {
                if (StringUtils.isNotEmpty(userOptional.get().getActivationKey())) {
                    JWTToken token = new JWTToken();
                    token.setNeedRegister(true);
                    return new ResponseEntity<>(token, HttpStatus.OK);
                }
            }
        } else {
            String lowercaseLogin = loginVM.getUsername().toLowerCase(Locale.ENGLISH);
            userOptional = userService.findOneByLogin(lowercaseLogin);
            if (userOptional.isPresent()) {
                if (StringUtils.isNotEmpty(userOptional.get().getActivationKey())) {
                    JWTToken token = new JWTToken();
                    token.setNeedRegister(true);
                    return new ResponseEntity<>(token, HttpStatus.OK);
                }
            }
        }

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        String jwt = tokenProvider.createToken(authentication, rememberMe);

        String login = userOptional.get().getLogin();
        redissonTokenStore.putInRedis(login, jwt);

        List<Long> userDivisionIds = userService.findAllUserDivisionIds(login);
        redissonTokenStore.storeUserDivision(login, userDivisionIds);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @ApiOperation("用户登出")
    public ResponseEntity<Void> logout() {
        redissonTokenStore.removeFromRedis(SecurityUtils.getCurrentUserLogin().orElse(""));
        redissonTokenStore.removeDivisionIdsFromRedis(SecurityUtils.getCurrentUserLogin().orElse(""));
        return ResponseEntity.noContent().build();
    }

    /**
     * Object to return as body in JWT Authentication.
     */

    static class JWTToken {

        @ApiModelProperty(value = "JWT令牌, 后续请求需设置在http header")
        private String idToken;

        @ApiModelProperty(value = "是否第一次登陆，需要注册邮件")
        private boolean needRegister;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        public JWTToken() {
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }

        public boolean isNeedRegister() {
            return needRegister;
        }

        public void setNeedRegister(boolean needRegister) {
            this.needRegister = needRegister;
        }
    }
}
