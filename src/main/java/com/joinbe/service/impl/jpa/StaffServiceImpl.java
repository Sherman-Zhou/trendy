package com.joinbe.service.impl.jpa;

import com.joinbe.common.error.EmailAlreadyUsedException;
import com.joinbe.common.error.InvalidPasswordException;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.config.Constants;
import com.joinbe.domain.*;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.domain.enumeration.Sex;
import com.joinbe.repository.*;
import com.joinbe.security.AuthoritiesConstants;
import com.joinbe.security.RedissonTokenStore;
import com.joinbe.security.SecurityUtils;
import com.joinbe.security.UserLoginInfo;
import com.joinbe.service.RoleService;
import com.joinbe.service.StaffService;
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
public class StaffServiceImpl implements StaffService {

    private final Logger log = LoggerFactory.getLogger(StaffServiceImpl.class);

    private final StaffRepository staffRepository;

    private final SystemUserRepository systemUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final RoleService roleService;

    private final CacheManager cacheManager;

    private final ShopRepository shopRepository;

    private final CityRepository cityRepository;

    private final PermissionRepository permissionRepository;

    private final RedissonTokenStore redissonTokenStore;

    public StaffServiceImpl(StaffRepository staffRepository, SystemUserRepository systemUserRepository, PasswordEncoder passwordEncoder,
                            RoleRepository roleRepository, CacheManager cacheManager, RoleService roleService, ShopRepository shopRepository,
                            CityRepository cityRepository, PermissionRepository permissionRepository,
                            RedissonTokenStore redissonTokenStore) {
        this.staffRepository = staffRepository;
        this.systemUserRepository = systemUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.cacheManager = cacheManager;
        this.roleService = roleService;
        this.shopRepository = shopRepository;
        this.cityRepository = cityRepository;
        this.permissionRepository = permissionRepository;
        this.redissonTokenStore = redissonTokenStore;
    }

    @Override
    public Optional<Staff> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return staffRepository.findOneByActivationKey(key)
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
    public Optional<Staff> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return staffRepository.findOneByResetKey(key)
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
    public Optional<Staff> requestPasswordReset(String mail) {
        return staffRepository.findOneByEmailIgnoreCaseAndStatusNot(mail, RecordStatus.DELETED)
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    @Override
    public Optional<Staff> requestPasswordReset(Long userId) {
        return staffRepository.findOneWithRolesById(userId)
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                SecurityUtils.checkMerchantPermission(user.getMerchant());
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    @Override
    public Optional<Staff> registerUserEmail(UserRegisterVM userDTO) {
        staffRepository.findOneByEmailIgnoreCaseAndStatusNot(userDTO.getEmail(), RecordStatus.DELETED).ifPresent(existingUser -> {
            if (!existingUser.getLogin().equals(userDTO.getLogin()) && existingUser.getActivated()) {
                throw new EmailAlreadyUsedException();
            }
        });

        return Optional.of(staffRepository
            .findOneWithRolesByLogin(userDTO.getLogin()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            //not allow to update deleted user
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                user.setEmail(userDTO.getEmail().toLowerCase());
                user.setStatus(RecordStatus.ACTIVE);
                user.setActivationKey(null);
                staffRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Created Information for User: {}", user);
                return user;
            });
    }

    @Override
    public Optional<Staff> changeUserEmail(ChangeEmailVM userDTO) {

        staffRepository.findOneByEmailIgnoreCaseAndStatusNot(userDTO.getEmail(), RecordStatus.DELETED).ifPresent(existingUser -> {
            if (!existingUser.getLogin().equals(userDTO.getLogin()) && existingUser.getActivated()) {
                throw new EmailAlreadyUsedException();
            }
        });
        Optional<Staff> userInDb = staffRepository.findOneWithRolesByLogin(userDTO.getLogin())
            //not allow to update deleted user
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()));
        if (!userInDb.isPresent()) {
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
                staffRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Created Information for User: {}", user);
                return user;
            });
    }


    private boolean removeNonActivatedUser(Staff existingStaff) {
        if (existingStaff.getActivated()) {
            return false;
        }
        staffRepository.delete(existingStaff);
        staffRepository.flush();
        this.clearUserCaches(existingStaff);
        return true;
    }

    @Override
    public Staff createUser(UserDTO userDTO) {
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        Staff staff = new Staff();
        staff.setLogin(userDTO.getLogin().toLowerCase());
        staff.setName(userDTO.getName());

        if (userDTO.getEmail() != null) {
            staff.setEmail(userDTO.getEmail().toLowerCase());
        }

        if (loginInfo.isSystemAdmin()) {
            log.debug("system admin update user");
        } else {
            Merchant merchant = new Merchant();
            merchant.setId(loginInfo.getMerchantId());
            staff.setMerchant(merchant);
        }

        staff.setAvatar(userDTO.getAvatar());
        if (userDTO.getLangKey() == null) {
            staff.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            staff.setLangKey(userDTO.getLangKey());
        }
        staff.setSex(Sex.resolve(userDTO.getSex()));
        staff.setMobileNo(userDTO.getMobileNo());
        if (StringUtils.isNotEmpty(userDTO.getPassword())) {
            String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
            staff.setPassword(encryptedPassword);
        }
        staff.setRemark(userDTO.getRemark());
        staff.setAddress(userDTO.getAddress());

        staff.setResetKey(RandomUtil.generateResetKey());
        staff.setResetDate(Instant.now());
        staff.setStatus(RecordStatus.INACTIVE);
        staff.setActivationKey(RandomUtil.generateActivationKey());

        if (!CollectionUtils.isEmpty(userDTO.getRoleIds())) {
            Set<Role> roles = userDTO.getRoleIds().stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            staff.setRoles(roles);
        } else {
            //if not selected any roles, set to user by default.
            Set<Role> roles = new HashSet<>();
            roleRepository.findByCodeAndStatusIs(AuthoritiesConstants.USER, RecordStatus.ACTIVE).ifPresent(roles::add);
            staff.setRoles(roles);
        }
        staffRepository.save(staff);
        this.clearUserCaches(staff);
        log.debug("Created Information for User: {}", staff);
        return staff;
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
            .flatMap(login -> staffRepository.findOneByLoginAndStatusNot(login, RecordStatus.DELETED))
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
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        return Optional.of(staffRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            //not allow to update deleted user
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                this.clearUserCaches(user);
                if (loginInfo.isSystemAdmin()) {
                    log.debug("system admin update user");
                } else {
                    SecurityUtils.checkMerchantPermission(loginInfo, user.getMerchant());

                }

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

                staffRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    @Override
    public Optional<UserDTO> updateUserStatus(Long id, RecordStatus status) {
        return Optional.of(staffRepository
            .findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            //not allow to update deleted user
            .filter(user -> !RecordStatus.DELETED.equals(user.getStatus()))
            .map(user -> {
                SecurityUtils.checkMerchantPermission(user.getMerchant());
                user.setStatus(status);
                staffRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Disabled/enable User: {}: {}", user.getLogin(), status);
                return user;
            })
            .map(UserDTO::new);
    }

    @Override
    public void deleteUser(Long id) {
        staffRepository.findById(id).ifPresent(user -> {
            // userRepository.delete(user);
            SecurityUtils.checkMerchantPermission(user.getMerchant());
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
            .flatMap(login -> staffRepository.findOneByLoginAndStatusNot(login, RecordStatus.DELETED))
            .ifPresent(user -> {
                SecurityUtils.checkMerchantPermission(user.getMerchant());
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
        QueryParams<Staff> queryParams = new QueryParams<>();
        queryParams.setDistinct(true);
//        Set<Division> userDivisions = SecurityUtils.getCurrentUserDivisionIds()
//            .stream().map(id -> new Division(id)).collect(Collectors.toSet());
        //queryParams.and("divisions", Filter.Operator.in, userDivisions);
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        if (loginInfo.isSystemAdmin()) {
            log.debug("system user...");
        } else {
            queryParams.and("merchant.id", Filter.Operator.eq, loginInfo.getMerchantId());
        }
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

        Specification<Staff> specification = Specification.where(queryParams);
        if (StringUtils.isNotEmpty(vm.getName())) {
            //name or account search...
            Specification<Staff> itemSpecification = (Specification<Staff>) (root, criteriaQuery, criteriaBuilder) -> {
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
        return staffRepository.findAll(specification, pageable).map(UserDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetailsDTO> getUserWithAuthoritiesByLogin(String login) {

        Optional<UserDetailsDTO> userOptional = staffRepository.findOneWithRolesByLogin(login).map(UserDetailsDTO::new);
        userOptional.ifPresent(userDetailsDTO -> SecurityUtils.checkMerchantPermission(userDetailsDTO.getMerchantId()));
        return userOptional;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetailsDTO> getUserEmail(String login) {

        return staffRepository.findOneWithRolesByLogin(login)
            .map(staff -> {
                UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
                userDetailsDTO.setId(staff.getId());
                userDetailsDTO.setLogin(staff.getLogin());
                userDetailsDTO.setEmail(staff.getEmail());
                return userDetailsDTO;
            });
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> getUserWithAuthorities(Long id) {
        return staffRepository.findOneWithRolesById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> getUserWithShopsAndCities() {
        return staffRepository.findOneWithShopsAndCitiesByLogin(SecurityUtils.getCurrentUserLogin().get());
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetailsDTO> getUserWithAuthorities() {
        Optional<String> userIdOptional = SecurityUtils.getCurrentUserLogin();
        if (userIdOptional.isPresent()) {
            String userId = userIdOptional.get();
            if (Constants.ADMIN_ACCOUNT.equalsIgnoreCase(userId)) {
                return SecurityUtils.getCurrentUserLogin()
                    .flatMap(systemUserRepository::findOneWithRoleByLogin)
                    .map(UserDetailsDTO::new);
            } else {
                return SecurityUtils.getCurrentUserLogin()
                    .flatMap(staffRepository::findOneWithRolesByLogin)
                    .map(UserDetailsDTO::new);
            }

        } else {
            return Optional.empty();
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetailsDTO> getSystemUserWithAuthorities(String login) {
        return systemUserRepository.findOneWithRoleByLogin(login)
            .map(UserDetailsDTO::new);
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
    public List<RoleDTO> getRolesForMerchant() {
        return roleRepository.findAllByRoleTypeAndStatus(Constants.ROLE_TYPE_MERCHANT, RecordStatus.ACTIVE)
            .stream().map(RoleService::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDetailsDTO> findOneByEmailIgnoreCase(String email) {
        return staffRepository.findOneByEmailIgnoreCaseAndStatusNot(email, RecordStatus.DELETED).map(UserDetailsDTO::new);
    }

    @Override
    public Optional<UserDetailsDTO> findOneByLogin(String login) {
        return staffRepository.findOneByLoginAndStatusNot(login, RecordStatus.DELETED).map(UserDetailsDTO::new);
    }

    @Override
    public List<Permission> findAllUserPermissionsByLogin(String login) {
        List<Permission> permissions = Constants.ADMIN_ACCOUNT.equalsIgnoreCase(login) ? permissionRepository.findAllBySystemUserLogin(login
        ) : permissionRepository.findAllByUserLogin(login);
        log.debug("user permissions:{}", permissions.size());
        return permissions;
    }

    @Override
    public UserDTO assignDivision(Long userId, List<String> divisionIds) {
        UserDTO userDTO;
        Optional<Staff> userOptional = staffRepository.findById(userId);

        if (userOptional.isPresent()) {
            Staff staff = userOptional.get();
            SecurityUtils.checkMerchantPermission(staff.getMerchant());

            staff.getShops().clear();
            staff.getCities().clear();
            divisionIds.forEach(id -> {
                if (id.startsWith(Constants.CITY_ID_PREFIX)) {
                    City city = new City();
                    city.setId(id);
                    staff.getCities().add(city);
                } else {
                    Shop shop = new Shop();
                    shop.setId(id);
                    staff.getShops().add(shop);
                }
            });
            this.clearUserCaches(staff);
            userDTO = new UserDTO(staff);
        } else {
            throw new BadRequestAlertException("Invalid id", "User", "idnull");
        }
        return userDTO;
    }

    @Override
    public UserDTO assignMerchant(Long userId, Long merchantId) {
        UserDTO userDTO;
        Optional<Staff> userOptional = staffRepository.findById(userId);

        if (userOptional.isPresent()) {
            Staff staff = userOptional.get();
            Merchant merchant = new Merchant();
            merchant.setId(merchantId);
            staff.setMerchant(merchant);
            staff.setShops(shopRepository.findByMerchant(staff.getMerchant()));
            staff.setCities(cityRepository.findByMerchant(staff.getMerchant()));
            this.clearUserCaches(staff);
            userDTO = new UserDTO(staff);
        } else {
            throw new BadRequestAlertException("Invalid id", "User", "idnull");
        }
        return userDTO;
    }

    @Override
    public List<String> findAllUserDivisionIds(Long userId) {
        Optional<Staff> user = staffRepository.findOneWithDivisionsById(userId);
        if (user.isPresent()) {
            List<String> ids = user.get().getShops().stream().map(Shop::getId).collect(Collectors.toList());
            List<String> cityIds = user.get().getCities().stream().map(City::getId).collect(Collectors.toList());
            ids.addAll(cityIds);
            return ids;
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> findAllUserDivisionIds(String login) {
        Optional<Staff> user = staffRepository.findOneWithDivisionsByLogin(login);
        if (user.isPresent()) {
            List<String> ids = user.get().getShops().stream().map(Shop::getId).collect(Collectors.toList());
            List<String> cityIds = user.get().getCities().stream().map(City::getId).collect(Collectors.toList());
            ids.addAll(cityIds);
            return ids;
        }
        return new ArrayList<>();
    }


    private void clearUserCaches(Staff staff) {
//        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
//        if (user.getEmail() != null) {
//            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
//        }
    }
}
