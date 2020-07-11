package com.joinbe.service.impl.jpa;

import com.joinbe.common.error.EmailAlreadyUsedException;
import com.joinbe.common.error.InvalidPasswordException;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.config.Constants;
import com.joinbe.domain.Division;
import com.joinbe.domain.Permission;
import com.joinbe.domain.Role;
import com.joinbe.domain.User;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.domain.enumeration.Sex;
import com.joinbe.repository.PermissionRepository;
import com.joinbe.repository.RoleRepository;
import com.joinbe.repository.UserRepository;
import com.joinbe.security.AuthoritiesConstants;
import com.joinbe.security.RedissonTokenStore;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.RoleService;
import com.joinbe.service.UserService;
import com.joinbe.service.dto.RoleDTO;
import com.joinbe.service.dto.UserDTO;
import com.joinbe.service.dto.UserDetailsDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.ChangeEmailVM;
import com.joinbe.web.rest.vm.UserRegisterVM;
import com.joinbe.web.rest.vm.UserVM;
import io.github.jhipster.security.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service("JpaUserService")
@Transactional
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final RoleService roleService;

    private final CacheManager cacheManager;

    private final PermissionRepository permissionRepository;

    private final RedissonTokenStore redissonTokenStore;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                           CacheManager cacheManager, RoleService roleService, PermissionRepository permissionRepository,
                           RedissonTokenStore redissonTokenStore) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.cacheManager = cacheManager;
        this.roleService = roleService;
        this.permissionRepository = permissionRepository;
        this.redissonTokenStore = redissonTokenStore;
    }

    @Override
    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setStatus(RecordStatus.ACTIVE);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    @Override
    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                this.clearUserCaches(user);
                return user;
            });
    }

    @Override
    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCaseAndStatusNot(mail, RecordStatus.DELETED)
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    @Override
    public Optional<User> requestPasswordReset(Long userId) {
        return userRepository.findOneWithRolesById(userId)
            .filter(user-> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    @Override
    public Optional<User> registerUserEmail(UserRegisterVM userDTO) {
        userRepository.findOneByEmailIgnoreCaseAndStatusNot(userDTO.getEmail(), RecordStatus.DELETED).ifPresent(existingUser -> {
            if (!existingUser.getLogin().equals(userDTO.getLogin()) && existingUser.getActivated()) {
                throw new EmailAlreadyUsedException();
            }
        });

        return Optional.of(userRepository
            .findOneWithRolesByLogin(userDTO.getLogin()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            //not allow to update deleted user
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                user.setEmail(userDTO.getEmail().toLowerCase());
                user.setStatus(RecordStatus.ACTIVE);
                user.setActivationKey(null);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Created Information for User: {}", user);
                return user;
            });
    }

    @Override
    public Optional<User> changeUserEmail(ChangeEmailVM userDTO) {

        userRepository.findOneByEmailIgnoreCaseAndStatusNot(userDTO.getEmail(), RecordStatus.DELETED).ifPresent(existingUser -> {
            if (!existingUser.getLogin().equals(userDTO.getLogin()) && existingUser.getActivated()) {
                throw new EmailAlreadyUsedException();
            }
        });
        Optional <User> userInDb = userRepository.findOneWithRolesByLogin(userDTO.getLogin())
            //not allow to update deleted user
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()));
        if(!userInDb.isPresent()) {
            throw new InvalidPasswordException();
        }

        return Optional.of(userInDb)
            //.filter(Optional::isPresent)
            .map(Optional::get)
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(userDTO.getPassword(), currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                user.setOldEmail(user.getEmail());
                user.setEmail(userDTO.getEmail().toLowerCase());
                // user.setStatus(RecordStatus.ACTIVE);
                // user.setActivationKey(null);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Created Information for User: {}", user);
                return user;
            });
    }


    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.getActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        return true;
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
        user.setSex(Sex.resolve(userDTO.getSex()));
        user.setMobileNo(userDTO.getMobileNo());
        if(StringUtils.isNotEmpty(userDTO.getPassword())){
            String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
            user.setPassword(encryptedPassword);
        }
        user.setRemark(userDTO.getRemark());
        user.setAddress(userDTO.getAddress());

        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setStatus(RecordStatus.INACTIVE);
        user.setActivationKey(RandomUtil.generateActivationKey());

        if (!CollectionUtils.isEmpty(userDTO.getRoleIds())) {
            Set<Role> roles = userDTO.getRoleIds().stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            //if not selected any roles, set to user by default.
            Set<Role> roles = new HashSet<>();
            roleRepository.findByCodeAndStatusIs(AuthoritiesConstants.USER, RecordStatus.ACTIVE).ifPresent(roles::add);
            user.setRoles(roles);
        }
        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param name    name of user.
     * @param email   email id of user.
     * @param langKey language key.
     * @param address image URL of user.
     */
    @Override
    public void updateUser(String name, String email, String langKey, String address, String mobileNo) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> userRepository.findOneByLoginAndStatusNot(login, RecordStatus.DELETED))
            .ifPresent(user -> {
                user.setName(name);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setAddress(address);
                user.setMobileNo(mobileNo);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
            });
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    @Override
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            //not allow to update deleted user
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                this.clearUserCaches(user);
                // SecurityUtils.checkDataPermission(user.getDivisions());
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setName(userDTO.getName());

                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setAddress(userDTO.getAddress());
                user.setRemark(userDTO.getRemark());
                if (StringUtils.isNotEmpty(userDTO.getPassword())) {
                    String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
                    user.setPassword(encryptedPassword);
                }
                user.setAvatar(userDTO.getAvatar());
                user.setStatus(RecordStatus.resolve(userDTO.getStatus()));
                user.setLangKey(userDTO.getLangKey());
                user.setSex(Sex.resolve(userDTO.getSex()));
                user.setMobileNo(userDTO.getMobileNo());
                // user.setVersion(userDTO.getVersion());
                Set<Role> managedAuthorities = user.getRoles();
                managedAuthorities.clear();
                userDTO.getRoleIds().stream()
                    .map(roleRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);

                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    @Override
    public Optional<UserDTO> updateUserStatus(Long id, RecordStatus status) {
        return Optional.of(userRepository
            .findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            //not allow to update deleted user
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                // SecurityUtils.checkDataPermission(user.getDivisions());
                user.setStatus(status);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Disabled/enable User: {}: {}", user.getLogin(), status);
                return user;
            })
            .map(UserDTO::new);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            // userRepository.delete(user);
            // SecurityUtils.checkDataPermission(user.getDivisions());
            user.setStatus(RecordStatus.DELETED);
            String deleteLogin = user.getLogin().length() >= 48 ? user.getLogin().substring(0, 48) : user.getLogin();
            user.setLogin(deleteLogin + "$D");
            this.clearUserCaches(user);
            log.debug("Deleted User: {}", user);
        });
    }

    @Override
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> userRepository.findOneByLoginAndStatusNot(login, RecordStatus.DELETED))
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable, UserVM vm) {
        QueryParams<User> queryParams = new QueryParams<>();
        queryParams.setDistinct(true);
//        Set<Division> userDivisions = SecurityUtils.getCurrentUserDivisionIds()
//            .stream().map(id -> new Division(id)).collect(Collectors.toSet());
        //queryParams.and("divisions", Filter.Operator.in, userDivisions);
        queryParams.and("status", Filter.Operator.ne, RecordStatus.DELETED);

        if (StringUtils.isNotEmpty(vm.getEmail())) {
            queryParams.and("email", Filter.Operator.eq, vm.getEmail());
        }

        if (StringUtils.isNotEmpty(vm.getLangKey())) {
            queryParams.and("langKey", Filter.Operator.eq, vm.getLangKey());
        }

        if (StringUtils.isNotEmpty(vm.getAddress())) {
            queryParams.and("address", Filter.Operator.like, vm.getAddress());
        }
        if (StringUtils.isNotEmpty(vm.getMobileNo())) {
            queryParams.and("mobileNo", Filter.Operator.like, vm.getMobileNo());
        }

        Specification<User> specification = Specification.where(queryParams);
        if (StringUtils.isNotEmpty(vm.getName())) {
            //name or account search...
            Specification<User> itemSpecification = (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {
                Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + vm.getName().trim() + "%");
                Predicate loginPredicate = criteriaBuilder.like(root.get("login"), "%" + vm.getName().trim() + "%");
                return criteriaBuilder.or(namePredicate, loginPredicate);
            };
            specification = specification.and(itemSpecification);
        }

        //Divisions Id
//        Specification<User> divisionSpecification = (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {
//             return root.join("divisions").in(userDivisions);
//        };
//        specification = specification.and(divisionSpecification);
        return userRepository.findAll(specification, pageable).map(UserDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetailsDTO> getUserWithAuthoritiesByLogin(String login) {

        Optional<UserDetailsDTO> userOptional = userRepository.findOneWithRolesByLogin(login).map(UserDetailsDTO::new);
//        if(userOptional.isPresent()){
//            SecurityUtils.checkDataPermission(userOptional.get().getDivisions());
//        }
        return userOptional;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(Long id) {
        return userRepository.findOneWithRolesById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetailsDTO> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithRolesByLogin).map(UserDetailsDTO::new);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
//        userRepository
//            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
//            .forEach(user -> {
//                log.debug("Deleting not activated user {}", user.getLogin());
//                userRepository.delete(user);
//                this.clearUserCaches(user);
//            });
    }

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    @Override
    public List<RoleDTO> getRoles() {
        return roleRepository.findAllByStatus(RecordStatus.ACTIVE).stream().map(RoleService::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<User> findOneByEmailIgnoreCase(String email) {
        return userRepository.findOneByEmailIgnoreCaseAndStatusNot(email, RecordStatus.DELETED);
    }

    @Override
    public Optional<User> findOneByLogin(String login) {
        return userRepository.findOneByLoginAndStatusNot(login, RecordStatus.DELETED);
    }

    @Override
    public List<Permission> findAllUserPermissionsByLogin(String login) {
        List<Permission> permissions = permissionRepository.findAllByUserLogin(login);
        log.debug("user permissions:{}", permissions.size());
        return permissions;
    }

    @Override
    public UserDTO assignDivision(Long userId, List<Long> divisionIds) {
        UserDTO userDTO;
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            //to check if the admin user has the permission of the division Ids to be assign
//            SecurityUtils.checkDataPermission(divisionIds);
//            SecurityUtils.checkDataPermission(user.getDivisions());
            user.getDivisions().clear();

            divisionIds.forEach(divisionId -> {
                Division division = new Division();
                division.setId(divisionId);
                user.addDivision(division);
            });
            this.clearUserCaches(user);
            userDTO = new UserDTO(user);
        } else {
            throw new BadRequestAlertException("Invalid id", "User", "idnull");
        }
        return userDTO;
    }

    @Override
    public List<Long> findAllUserDivisionIds(Long userId) {
        Optional<User> user = userRepository.findOneWithDivisionsById(userId);
        if(user.isPresent()){
            List<Long > ids = user.get().getDivisions().stream().map(Division::getId).sorted().collect(Collectors.toList());
            return ids;
        }
        return new ArrayList<>();
    }

    @Override
    public List<Long> findAllUserDivisionIds(String login) {
        Optional<User> user = userRepository.findOneWithDivisionsByLogin(login);
        if(user.isPresent()){
            List<Long > ids = user.get().getDivisions().stream().map(Division::getId).sorted().collect(Collectors.toList());
            return ids;
        }
        return new ArrayList<>();
    }


    private void clearUserCaches(User user) {
//        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
//        if (user.getEmail() != null) {
//            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
//        }
    }
}
