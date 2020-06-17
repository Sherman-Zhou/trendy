package com.joinbe.service.impl.mp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joinbe.common.util.PageUtil;
import com.joinbe.config.Constants;
import com.joinbe.domain.Permission;
import com.joinbe.domain.User;
import com.joinbe.domain.UserRole;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.mp.mapper.UserMapper;
import com.joinbe.service.UserRoleService;
import com.joinbe.service.UserService;
import com.joinbe.service.dto.RoleDTO;
import com.joinbe.service.dto.UserDTO;
import com.joinbe.service.dto.UserDetailsDTO;
import com.joinbe.web.rest.vm.ChangeEmailVM;
import com.joinbe.web.rest.vm.UserRegisterVM;
import com.joinbe.web.rest.vm.UserVM;
import io.github.jhipster.security.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;

/**
 * Service class for managing users.
 */
@Service("mpUserService")
@Transactional
public class MpUserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final Logger log = LoggerFactory.getLogger(MpUserServiceImpl.class);

    private final UserRoleService userRoleService;

    private final PasswordEncoder passwordEncoder;

    public MpUserServiceImpl(PasswordEncoder passwordEncoder, UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<UserDTO> getAllManagedUsers(Pageable pageable, UserVM userVM) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(userVM.getName()), "first_name", userVM.getName());

        List<Sort.Order> orders = pageable.getSort().toList();
        for (Sort.Order order : orders) {
            if (Sort.Direction.ASC.equals(order.getDirection())) {
                queryWrapper.orderByAsc(order.getProperty());
            } else {
                queryWrapper.orderByDesc(order.getProperty());
            }
        }
        Page<User> userPage = PageUtil.toSpringDataPage(page(PageUtil.toMpPage(pageable), queryWrapper), pageable);
        return userPage.map(UserDTO::new);
    }


    @Override
    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setName(userDTO.getName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setAvatar(userDTO.getAvatar());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setStatus(RecordStatus.ACTIVE);
        user.setVersion(0);

        this.save(user);

        if (!CollectionUtils.isEmpty(userDTO.getRoleIds())) {
            List<UserRole> userRoles = new ArrayList<>(userDTO.getRoleIds().size());
            Set<Long> roleIds = new HashSet<>(userDTO.getRoleIds());
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoles.add(userRole);
            }
            userRoleService.saveBatch(userRoles);
        }
        log.debug("Created Information for User: {}", user);
        return user;
    }

    @Override
    public void updateUser(String name, String email, String langKey, String imageUrl) {

    }

    @Override
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.empty();
    }

    @Override
    public Optional<UserDTO> updateUserStatus(Long id, RecordStatus status) {
        return Optional.empty();
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public Optional<User> activateRegistration(String key) {
        return Optional.empty();
    }

    @Override
    public Optional<User> completePasswordReset(String newPassword, String key) {
        return Optional.empty();
    }

    @Override
    public Optional<User> requestPasswordReset(String mail) {
        return Optional.empty();
    }

    @Override
    public Optional<User> requestPasswordReset(Long userId) {
        return Optional.empty();
    }

    @Override
    public Optional<User> registerUserEmail(UserRegisterVM userDTO) {
        return Optional.empty();
    }

    @Override
    public Optional<User> changeUserEmail(ChangeEmailVM userDTO) {
        return Optional.empty();
    }


    @Override
    public void changePassword(String currentClearTextPassword, String newPassword) {

    }


    @Override
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserWithAuthorities(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<UserDetailsDTO> getUserWithAuthorities() {
        return Optional.empty();
    }

    @Override
    public List<RoleDTO> getRoles() {
        return null;
    }

    @Override
    public Optional<User> findOneByEmailIgnoreCase(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findOneByLogin(String login) {
        return Optional.empty();
    }

    @Override
    public List<Permission> findAllUserPermissionsByLogin(String login) {
        return null;
    }

    @Override
    public UserDTO assignDivision(Long userId, List<Long> divisionIds) {
        return null;
    }

    @Override
    public List<Long> findAllUserDivisionIds(Long userId) {
        return null;
    }
}
