package com.joinbe.security;


import com.joinbe.domain.Merchant;
import com.joinbe.domain.Shop;
import com.joinbe.service.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.CredentialsExpiredException;
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

    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

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

    public static UserLoginInfo getCurrentUserLoginInfo() {
        Optional<String> login = getCurrentUserLogin();
        RedissonTokenStore redissonTokenStore = SpringContextUtils.getBean(RedissonTokenStore.class);
        if (login.isPresent()) {
            return redissonTokenStore.getUserLoginInfo(login.get());
        } else {
            throw new CredentialsExpiredException("User Login info is expired");
        }
    }

    /**
     * to check if the user has the permission of Division
     *
     * @param shop
     */
    public static void checkDataPermission(Shop shop) {
        if (shop == null) {
            throw new AccessDeniedException("No Permission to view this record");
        }
        checkDataPermission(getCurrentUserLoginInfo(), shop.getId());
    }

    /**
     * to check if the user has the permission of Division
     *
     * @param shop
     */
    public static void checkDataPermission(UserLoginInfo loginInfo, Shop shop) {
        if (shop == null) {
            log.warn("the user {} try to access data of shop {}  ", getCurrentUserLoginInfo().getLogin(), shop);
            throw new AccessDeniedException("No Permission to view this record");
        }
        checkDataPermission(loginInfo, shop.getId());
    }

    /**
     * to check if the user has the permission of Division
     *
     * @param shopId
     */
    public static void checkDataPermission(UserLoginInfo loginInfo, String shopId) {

        if (!loginInfo.getDivisionIds().contains(shopId)) {
            log.warn("the user {} try to access data from shop{}", loginInfo.getLogin(), shopId);
            throw new AccessDeniedException("No Permission to view this record");
        }
    }

    /**
     * to check if the user has the permission of Division
     *
     * @param shopId
     */
    public static void checkDataPermission(String shopId) {

        if (!getCurrentUserLoginInfo().getDivisionIds().contains(shopId)) {
            log.warn("the user {} try to access data from shop{}", getCurrentUserLoginInfo().getLogin(), shopId);
            throw new AccessDeniedException("No Permission to view this record");
        }
    }

    public static void checkMerchantPermission(Merchant merchant) {
        UserLoginInfo loginInfo = getCurrentUserLoginInfo();
        if (loginInfo.isSystemAdmin()) {
            return;
        }
        if (merchant == null || !merchant.getId().equals(loginInfo.getMerchantId())) {
            log.warn("the user {} try to access data from merchant{}", getCurrentUserLoginInfo().getLogin(), merchant);
            throw new AccessDeniedException("No Permission");
        }
    }

    public static void checkMerchantPermission(Long merchantId) {
        UserLoginInfo loginInfo = getCurrentUserLoginInfo();
        if (loginInfo.isSystemAdmin()) {
            return;
        }
        if (!merchantId.equals(loginInfo.getMerchantId())) {
            log.warn("the user {} try to access data from merchant{}", getCurrentUserLoginInfo().getLogin(), merchantId);
            throw new AccessDeniedException("No Permission");
        }
    }


    public static void checkMerchantPermission(UserLoginInfo loginInfo, Merchant merchant) {
        if (merchant == null || !merchant.getId().equals(loginInfo.getMerchantId())) {
            log.warn("the user {} try to access data from merchant{}", getCurrentUserLoginInfo().getLogin(), merchant);
            throw new AccessDeniedException("No Permission");
        }
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
