package com.joinbe.security;

import com.joinbe.common.error.UserNotActivatedException;
import com.joinbe.config.Constants;
import com.joinbe.domain.Permission;
import com.joinbe.domain.Staff;
import com.joinbe.domain.SystemUser;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.PermissionRepository;
import com.joinbe.repository.StaffRepository;
import com.joinbe.repository.SystemUserRepository;
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

    private final StaffRepository staffRepository;

    private final SystemUserRepository systemUserRepository;

    private final PermissionRepository permissionRepository;

    public DomainUserDetailsService(StaffRepository staffRepository, SystemUserRepository systemUserRepository,
                                    PermissionRepository permissionRepository) {
        this.staffRepository = staffRepository;
        this.systemUserRepository = systemUserRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        if (Constants.ADMIN_ACCOUNT.equalsIgnoreCase(login)) {
            return systemUserRepository.findOneWithRoleByLogin(lowercaseLogin)
                .map(user -> createSpringSecurityUser(lowercaseLogin, user))
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
        }

        if (new EmailValidator().isValid(login, null)) {
            return staffRepository.findOneWithRolesByEmailIgnoreCase(login)
                .map(user -> createSpringSecurityUser(login, user))
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + login + " was not found in the database"));
        }


        return staffRepository.findOneWithRolesByLogin(lowercaseLogin)
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

        return new org.springframework.security.core.userdetails.User(staff.getLogin(),
            staff.getPassword(),
            grantedAuthorities);
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, SystemUser staff) {
        if (!staff.getActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        List<Permission> permissions = permissionRepository.findAllBySystemUserLogin(staff.getLogin());
        List<GrantedAuthority> grantedAuthorities = permissions.stream()
            .filter(permission -> CollectionUtils.isEmpty(permission.getChildren())
                || permission.getChildren().stream().noneMatch(child -> RecordStatus.ACTIVE.equals(child.getStatus())))
            .map(permission -> new SimpleGrantedAuthority(permission.getPermissionKey())).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(staff.getLogin(),
            staff.getPassword(),
            grantedAuthorities);
    }
}
