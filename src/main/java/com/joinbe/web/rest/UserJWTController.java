package com.joinbe.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joinbe.config.Constants;
import com.joinbe.security.RedissonTokenStore;
import com.joinbe.security.SecurityUtils;
import com.joinbe.security.UserLoginInfo;
import com.joinbe.security.jwt.JWTFilter;
import com.joinbe.security.jwt.TokenProvider;
import com.joinbe.service.StaffService;
import com.joinbe.service.dto.UserDetailsDTO;
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
    private final StaffService staffService;


    public UserJWTController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder,
                             RedissonTokenStore redissonTokenStore,
                             @Qualifier("JpaUserService") StaffService staffService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.redissonTokenStore = redissonTokenStore;

        this.staffService = staffService;
    }

    @PostMapping("/authenticate")
    @ApiOperation("用户登陆")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        Optional<UserDetailsDTO> userOptional;

        boolean isAdmin = Constants.ADMIN_ACCOUNT.equalsIgnoreCase(loginVM.getUsername());
        String lowercaseLogin = loginVM.getUsername().toLowerCase(Locale.ENGLISH);
        if (!isAdmin) {
            if (new EmailValidator().isValid(loginVM.getUsername(), null)) {
                userOptional = staffService.findOneByEmailIgnoreCase(loginVM.getUsername());
            } else {

                userOptional = staffService.findOneByLogin(lowercaseLogin);
            }
            if (userOptional.isPresent()) {
                //need register email before login
                if (StringUtils.isNotEmpty(userOptional.get().getActivationKey())) {
                    JWTToken token = new JWTToken();
                    token.setNeedRegister(true);
                    return new ResponseEntity<>(token, HttpStatus.OK);
                }
            }
        } else {
            userOptional = staffService.getSystemUserWithAuthorities(lowercaseLogin);
        }
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        String jwt = tokenProvider.createToken(authentication, rememberMe);
        if (userOptional.isPresent()) {
            UserDetailsDTO userDetailsDTO = userOptional.get();
            UserLoginInfo loginInfo = new UserLoginInfo(userDetailsDTO.getLogin(), userDetailsDTO.getMerchantId(), userDetailsDTO.getDivisionIds());
            redissonTokenStore.saveLoginInfo(userDetailsDTO.getLogin(), loginInfo);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @ApiOperation("用户登出")
    public ResponseEntity<Void> logout() {
        SecurityUtils.getCurrentUserLogin().ifPresent(redissonTokenStore::removeLoginInfo);
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
