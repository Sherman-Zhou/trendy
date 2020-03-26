package com.joinbe.service.impl.mp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joinbe.common.util.PageUtil;
import com.joinbe.config.Constants;
import com.joinbe.domain.User;
import com.joinbe.domain.UserRole;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.mp.mapper.UserMapper;
import com.joinbe.service.UserRoleService;
import com.joinbe.service.UserService;
import com.joinbe.service.dto.RoleDTO;
import com.joinbe.service.dto.UserDTO;
import com.joinbe.web.rest.vm.ManagedUserVM;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Page<UserDTO> getAllManagedUsers(Pageable pageable, ManagedUserVM userVM) {
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

        this.save(user);

        if (userDTO.getAuthorities() != null) {
            List<UserRole> userRoles = new ArrayList<>(userDTO.getAuthorities().size());
//            for(Long roleId : userDTO.getAuthorities()){
//                UserRole userRole = new UserRole( );
//                userRole.setRoleId(roleId);
//                userRole.setUserId(user.getId());
//                userRoles.add(userRole);
//            }
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
    public void deleteUser(String login) {

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
    public User registerUser(UserDTO userDTO, String password) {
        return null;
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
    public Optional<User> getUserWithAuthorities() {
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
}
