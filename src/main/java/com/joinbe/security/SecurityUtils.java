package com.joinbe.security;


import com.joinbe.domain.Shop;
import com.joinbe.service.util.SpringContextUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {

    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    public static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    public static List<String> getCurrentUserDivisionIds() {
        Optional<String> currentUserLogin = getCurrentUserLogin();
        if (!currentUserLogin.isPresent()) {
            return new ArrayList<>();
        }
        RedissonTokenStore redissonTokenStore = SpringContextUtils.getBean(RedissonTokenStore.class);
        return redissonTokenStore.getUserDivisionIds(currentUserLogin.get());

    }

    /**
     * to check if the user has the permission of Division
     *
     * @param division
     */
    public static void checkDataPermission(Shop division) {
//        if(division == null) {
//            throw new AccessDeniedException("No Permission to view this record");
//        }
//        checkDataPermission(division.getId());
    }

    /**
     * to check if the user has the permission of Division
     *
     * @param divisionId
     */
    public static void checkDataPermission(String divisionId) {
//        if(!getCurrentUserDivisionIds().contains(divisionId)) {
//            throw new AccessDeniedException("No Permission to view this record");
//        }
    }


    //    /**
//     * to check if the current user has the permission of all divisions
//     *
//     * @param divisions
//     */
//    public static void checkDataPermission(Set<Division> divisions) {
//        List<Long> userDivisionIds = getCurrentUserDivisionIds();
////       boolean hasAllPermission= divisions.stream().allMatch(division -> userDivisionIds.contains(division.getId()));
////       if(!hasAllPermission) {
////           throw new AccessDeniedException("No Permission to view this record");
////       }
//    }
    public static void checkDataPermission(List<Long> divisionIds) {
//        List<Long> userDivisionIds = getCurrentUserDivisionIds();
//        boolean hasAllPermission= divisionIds.stream().allMatch(divisionId -> userDivisionIds.contains(divisionId));
//        if(!hasAllPermission) {
//            throw new AccessDeniedException("No Permission to view this record");
//        }
    }


    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .filter(authentication -> authentication.getCredentials() instanceof String)
            .map(authentication -> (String) authentication.getCredentials());
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
            getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * The name of this method comes from the {@code isUserInRole()} method in the Servlet API.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    public static boolean isCurrentUserInRole(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
            getAuthorities(authentication).anyMatch(authority::equals);
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority);
    }

}
