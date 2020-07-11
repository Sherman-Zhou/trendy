package com.joinbe.security;

import com.joinbe.common.error.UserNotActivatedException;
import com.joinbe.domain.Permission;
import com.joinbe.domain.Staff;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.PermissionRepository;
import com.joinbe.repository.UserRepository;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    private final PermissionRepository permissionRepository;

    public DomainUserDetailsService(UserRepository userRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);

        if (new EmailValidator().isValid(login, null)) {
            return userRepository.findOneWithRolesByEmailIgnoreCase(login)
                .map(user -> createSpringSecurityUser(login, user))
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + login + " was not found in the database"));
        }

        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        return userRepository.findOneWithRolesByLogin(lowercaseLogin)
            .map(user -> createSpringSecurityUser(lowercaseLogin, user))
            .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));

    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, Staff staff) {
        if (!staff.getActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        List<Permission> permissions = permissionRepository.findAllByUserLogin(staff.getLogin());
        List<GrantedAuthority> grantedAuthorities = permissions.stream()
            .filter(permission -> CollectionUtils.isEmpty(permission.getChildren())
                || permission.getChildren().stream().noneMatch(child -> RecordStatus.ACTIVE.equals(child.getStatus())))
            .map(permission -> new SimpleGrantedAuthority(permission.getPermissionKey())).collect(Collectors.toList());

//        List<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
//            .map(authority -> new SimpleGrantedAuthority(authority.getCode()))
//            .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(staff.getLogin(),
            staff.getPassword(),
            grantedAuthorities);
    }
}
