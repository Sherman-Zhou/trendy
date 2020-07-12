package com.joinbe.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.ProviderManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.UserDetailsAwareConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


public class CustomAuthenticationConfigurer<B extends ProviderManagerBuilder<B>, U extends UserDetailsService>
    extends UserDetailsAwareConfigurer<B, U> {

    private final U userDetailsService;
    private CustomAuthenticationProvider provider;


    public CustomAuthenticationConfigurer(U userDetailsService, CustomAuthenticationProvider provider, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.provider = provider;
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
    }

    /**
     * Gets the {@link UserDetailsService} that is used with the
     * {@link DaoAuthenticationProvider}
     *
     * @return the {@link UserDetailsService} that is used with the
     * {@link DaoAuthenticationProvider}
     */
    public U getUserDetailsService() {
        return userDetailsService;
    }

    public void configure(B builder) {
        provider = postProcess(provider);
        builder.authenticationProvider(provider);
    }
}
