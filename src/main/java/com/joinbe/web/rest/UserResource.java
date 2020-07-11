package com.joinbe.web.rest;

import com.joinbe.config.Constants;
import com.joinbe.domain.Staff;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.security.RedissonTokenStore;
import com.joinbe.service.MailService;
import com.joinbe.service.MerchantService;
import com.joinbe.service.StaffService;
import com.joinbe.service.dto.*;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.errors.EmailAlreadyUsedException;
import com.joinbe.web.rest.errors.LoginAlreadyUsedException;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import com.joinbe.web.rest.vm.UserOperation;
import com.joinbe.web.rest.vm.UserVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link Staff} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api")
@Api(value ="用户管理相关接口", tags={"用户管理相关接口"}, produces = "application/json" )
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);


    private final StaffService staffService;


    private final MailService mailService;

    private final MerchantService merchantService;

    private final RedissonTokenStore redissonTokenStore;

    public UserResource(@Qualifier("JpaUserService") StaffService staffService, MailService mailService,
                        MerchantService merchantService, RedissonTokenStore redissonTokenStore) {
        this.staffService = staffService;
        this.mailService = mailService;
        this.merchantService = merchantService;
        this.redissonTokenStore = redissonTokenStore;
    }

    /**
     * {@code POST  /users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException       if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping("/users")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    @ApiOperation(value = "创建新用户")
    public ResponseEntity<Staff> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (staffService.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (staffService.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            Staff newStaff = staffService.createUser(userDTO);
            //mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/users/" + newStaff.getLogin()))
                .body(newStaff);
        }
    }

    /**
     * {@code PUT /users} : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
     */
    @PutMapping("/users")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    @ApiOperation(value = "更新新用户")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        log.debug("REST request to update User : {}", userDTO);
        Optional<Staff> existingUser = staffService.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = staffService.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }
        Optional<UserDTO> updatedUser = staffService.updateUser(userDTO);

        return ResponseUtil.wrapOrNotFound(updatedUser);
    }

    /**
     * {@code GET /users/:id/:op} : disable/enable an existing User.
     *
     * @param id the id of the user to update.
     * @param op the operation: enable/disable.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     */
    @GetMapping("/users/{id}/{op}")
    @ApiOperation(value = "启用/停用用户", notes = "启用： enable， 停用： disable")
    public ResponseEntity<UserDTO> disableOrEnableUser(@PathVariable @ApiParam(value ="用户主键", required = true ) Long id,
                                                       @PathVariable @ApiParam(value ="操作类型", required = true ) UserOperation op) {
        log.debug("REST request to {} User : {} ", op, id);
        RecordStatus status = UserOperation.enable.equals(op) ? RecordStatus.ACTIVE : RecordStatus.INACTIVE;
        Optional<UserDTO> updatedUser = staffService.updateUserStatus(id, status);
        return ResponseUtil.wrapOrNotFound(updatedUser);
    }

    /**
     * {@code GET   /user/:id/reset} : Send an email to reset the password of the user.
     *
     * @param id the id of the user.
     */
    @GetMapping(path = "/user/{id}/reset")
    @ApiOperation(value = "重置用户密码")
    public ResponseEntity<UserDTO> requestPasswordReset(@PathVariable  @ApiParam(value ="用户主键", required = true ) Long id) {
        Optional<Staff> user = staffService.requestPasswordReset(id);
        user.ifPresent(mailService::sendPasswordResetMail);

        return ResponseUtil.wrapOrNotFound(user.map(UserDTO::new));
    }

    /**
     * {@code GET /users} : get all users.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    @ApiOperation("搜索用户")
    public ResponseEntity<PageData<UserDTO>> getAllUsers(Pageable pageable, UserVM userVM) {
        final Page<UserDTO> page = staffService.getAllManagedUsers(pageable, userVM);
        return ResponseUtil.toPageData(page);
    }

    /**
     * Gets all available roles.
     *
     * @return all available roles.
     */
    @GetMapping("/users/roles")
    @ApiOperation("获取所有平台角色")
    public List<RoleDTO> getRoles() {
        return staffService.getRolesForMerchant();
    }

    /**
     * Gets all available merchants.
     *
     * @return all available merchants.
     */
    @GetMapping("/users/merchants")
    @ApiOperation("获取所有平台")
    public List<MerchantDTO> getMerchants() {
        return merchantService.getAll().stream().map(MerchantService::toDto).collect(Collectors.toList());
    }

    /**
     * {@code Get  /users/{userId}/assign-merchant} : assign platform to user.
     *
     * @param userId     the id of the user to update.
     * @param merchantId the id  of merchant to assign.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userDto,
     * or with status {@code 500 (Internal Server Error)} if the userDto couldn't be updated.
     */
    @GetMapping("/users/{userId}/assign-merchant/{merchantId}")
    @ApiOperation("分配平台")
    public ResponseEntity<UserDTO> assignMerchant(@PathVariable @ApiParam(value = "用户主键", required = true) Long userId,
                                                  @PathVariable @ApiParam(value = "平台主键", required = true) Long merchantId) {
        log.debug("REST request to assign merchant: {} to user : {}", merchantId, userId);
        UserDTO result = staffService.assignMerchant(userId, merchantId);

        return ResponseEntity.ok()
            .body(result);
    }


    /**
     * {@code PUT  /users/{userId}/assign} : assign division to user.
     *
     * @param userId      the id of the user to update.
     * @param divisionIds the id  of division to assign.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userDto,
     * or with status {@code 500 (Internal Server Error)} if the userDto couldn't be updated.
     */
    @PutMapping("/users/{userId}/assign")
    @ApiOperation("分配用户城市/部门")
    public ResponseEntity<UserDTO> assignDivision(@PathVariable @ApiParam(value = "用户主键", required = true) Long userId,
                                                  @Valid @RequestBody @ApiParam(value = "城市/部门主键列表", required = true) List<String> divisionIds) {
        log.debug("REST request to assign division: {} to user : {}", divisionIds, userId);
        UserDTO result = staffService.assignDivision(userId, divisionIds);
        //redissonTokenStore.storeUserDivision(result.getLogin(), divisionIds);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * GET  /users/divisions: get all departments which current user has.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of divisions in body
     */
    @GetMapping("/users/divisions")
    @ApiOperation(value = "获取当前用户拥有的所有部门")
    public List<DivisionDTO> getCurrentUserDivisions() {
        log.debug("REST request to get all divisions");
        Optional<Staff> staffOptional = staffService.getUserWithShopsAndCities();
        if (staffOptional.isPresent()) {
            Staff staff = staffOptional.get();
            List<DivisionDTO> divisionDTOS = staff.getShops().stream().map(shop -> new DivisionDTO(shop, LocaleContextHolder.getLocale())).collect(Collectors.toList());

            List<DivisionDTO> divisionDTOInCities = staff.getCities().stream().map(shop -> new DivisionDTO(shop, LocaleContextHolder.getLocale())).collect(Collectors.toList());
            divisionDTOS.addAll(divisionDTOInCities);
            return divisionDTOS;
        }
        return new ArrayList<>();
    }

    /**
     * GET  /api/users/:userId/divisions: get all the user's division ids
     *
     * @param userId the user id
     * @return the ResponseEntity with status 200 (OK) and the list of division ids in body
     */
    @GetMapping("/users/{userId}/divisions")
    @ApiOperation(value = "获取用户部门主键列表")
    public List<String> getAllUserDivisionIds(@PathVariable @ApiParam(value = "用户主键", required = true) Long userId) {
        log.debug("REST request to get a List of children divisions for parent:{}", userId);
        return staffService.findAllUserDivisionIds(userId);
    }



    /**
     * {@code GET /users/:login} : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @ApiOperation("获取用户详情")
    public ResponseEntity<UserDetailsDTO> getUser(@PathVariable @ApiParam(value ="用户登陆id", required = true ) String login) {
        log.debug("REST request to get User : {}", login);
        return ResponseUtil.wrapOrNotFound(
            staffService.getUserWithAuthoritiesByLogin(login));
    }

    /**
     * {@code DELETE /users/:id} : delete the  User by Id.
     *
     * @param id the id of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{id}")
    @ApiOperation("删除用户")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteUser(@PathVariable @ApiParam(value ="用户主键", required = true ) Long id) {
        log.debug("REST request to delete User: {}", id);
        staffService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
