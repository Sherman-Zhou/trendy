package com.joinbe.web.rest;

import com.joinbe.config.Constants;
import com.joinbe.domain.Staff;
import com.joinbe.domain.enumeration.PermissionType;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.PermissionService;
import com.joinbe.service.StaffService;
import com.joinbe.service.dto.PasswordChangeDTO;
import com.joinbe.service.dto.PermissionDTO;
import com.joinbe.service.dto.UserAccountDTO;
import com.joinbe.service.dto.UserDetailsDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.errors.EmailAlreadyUsedException;
import com.joinbe.web.rest.errors.InvalidPasswordException;
import com.joinbe.web.rest.vm.ChangeEmailVM;
import com.joinbe.web.rest.vm.KeyAndPasswordVM;
import com.joinbe.web.rest.vm.ResponseUtil;
import com.joinbe.web.rest.vm.UserRegisterVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@Api(value = "当前登陆用户操作相关接口", tags = {"当前登陆用户操作相关接口"}, produces = "application/json")
public class AccountResource {

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException          {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    @ApiOperation(value = "更新当前用户部分信息", notes = "仅允许并且只更新用户姓名， 用户邮件， 用户语言，手机号码和 地址")
    public void saveAccount(@Valid @RequestBody UserAccountDTO userDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"));
        if (Constants.ADMIN_ACCOUNT.equalsIgnoreCase(userLogin)) {
            staffService.updateSystemUser(userDTO.getName(), userDTO.getEmail(),
                userDTO.getLangKey(), userDTO.getAddress(), userDTO.getMobileNo());
        } else {
            Optional<UserDetailsDTO> existingUser = staffService.findOneByEmailIgnoreCase(userDTO.getEmail());
            if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
                throw new EmailAlreadyUsedException();
            }
            Optional<UserDetailsDTO> user = staffService.findOneByLogin(userLogin);
            if (!user.isPresent()) {
                throw new AccountResourceException("User could not be found");
            }
            staffService.updateUser(userDTO.getName(), userDTO.getEmail(),
                userDTO.getLangKey(), userDTO.getAddress(), userDTO.getMobileNo());
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);


    private final StaffService staffService;

    //private final MailService mailService;

    private final PermissionService permissionService;

    public AccountResource(@Qualifier("JpaUserService") StaffService staffService, PermissionService permissionService) {

        this.staffService = staffService;
        // this.mailService = mailService;
        this.permissionService = permissionService;
    }

//    /**
//     * {@code GET  /activate} : activate the registered user.
//     *
//     * @param key the activation key.
//     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
//     */
//    @GetMapping("/activate")
//    public void activateAccount(@RequestParam(value = "key") String key) {
//        Optional<User> user = userService.activateRegistration(key);
//        if (!user.isPresent()) {
//            throw new AccountResourceException("No user was found for this activation key");
//        }
//    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    @ApiOperation("查看用户是否登陆")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    @ApiOperation("获取当前登陆用户信息")
    public UserDetailsDTO getAccount() {
        return staffService.getUserWithAuthorities()
            .map(userDTO -> {
                List<PermissionDTO> permissionAndMenu = staffService.findAllUserPermissionsByLogin(userDTO.getLogin()).stream()
                    .map(permissionService::toDto).collect(Collectors.toList());

                Map<Long, List<PermissionDTO>> children = permissionAndMenu.stream()
                    .filter(menu -> (menu.getParentId() != null && !PermissionType.OPERATION.equals(menu.getPermissionType()))).
                        collect(Collectors.groupingBy(PermissionDTO::getParentId));
                for (PermissionDTO menu : permissionAndMenu) {
                    if (children.get(menu.getId()) != null) {
                        menu.setChildren(children.get(menu.getId()).stream().sorted(Comparator.comparing(PermissionDTO::getSortOrder)).collect(Collectors.toList()));
                    }
                }
                List<PermissionDTO> parents = permissionAndMenu.stream().filter(menu -> menu.getParentId() == null)
                    .sorted(Comparator.comparing(PermissionDTO::getSortOrder)).collect(Collectors.toList());

                userDTO.setMenus(parents);
                List<PermissionDTO> permissions = permissionAndMenu.stream()
                    .filter(permission -> PermissionType.OPERATION.equals(permission.getPermissionType())).collect(Collectors.toList());
                userDTO.setPermissions(permissions);
                return userDTO;
            })
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST  /account/change-email} : changes the current user's email.
     *
     * @param registerVM current userId, password and new Email.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-email")
    @ApiOperation("修改绑定邮箱")
    public void changeEmail(@RequestBody ChangeEmailVM registerVM) {
        staffService.changeUserEmail(registerVM);
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    @ApiOperation("更改当前登陆用户密码")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        staffService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code GET /account/forgot-password/:login} : get the user email by login.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/account/forgot-password/{login:" + Constants.LOGIN_REGEX + "}")
    @ApiOperation("忘记密码")
    public ResponseEntity<UserDetailsDTO> forgotPassword(@PathVariable @ApiParam(value = "用户登陆id", required = true) String login) {
        log.debug("REST request to get User {} email", login);

        Optional<UserDetailsDTO> userDetailsDTO = staffService.getUserEmail(login);

        if (userDetailsDTO.isPresent()) {
            Optional<Staff> user = staffService.requestPasswordReset(userDetailsDTO.get().getEmail());

        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing login '{}'", login);
        }
        return ResponseUtil.wrapOrNotFound(userDetailsDTO);
    }

//    /**
//     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
//     *
//     * @param mail the mail of the user.
//     */
//    @PostMapping(path = "/account/reset-password/init")
//    @ApiOperation("申请重置用户密码")
//    public void requestPasswordReset(@RequestBody @ApiParam(value = "用户邮件", required = true) String mail) {
//        Optional<User> user = userService.requestPasswordReset(mail);
//        if (user.isPresent()) {
//            mailService.sendPasswordResetMail(user.get());
//        } else {
//            // Pretend the request has been successful to prevent checking which emails really exist
//            // but log that an invalid attempt has been made
//            log.warn("Password reset requested for non existing mail '{}'", mail);
//        }
//    }

    /**
     * {@code POST   /account/forgot-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException         {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @ApiOperation("使用重置密匙重置用户密码")
    @PostMapping(path = "/account/forgot-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<Staff> user =
            staffService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    /**
     * {@code POST  /account/register} : register email for the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     */
    @PostMapping("/account/register")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("注册用户邮件")
    public void registerEmail(@Valid @RequestBody UserRegisterVM managedUserVM) {

        Optional<Staff> userOptional = staffService.registerUserEmail(managedUserVM);

    }

//    /**
//     * {@code GET  /account/activate} : activate the registered user.
//     *
//     * @param key the activation key.
//     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
//     */
//    @GetMapping("/account/activate")
//    @ApiOperation("激活邮件")
//    public void activateAccount(@RequestParam(value = "key") String key) {
//        Optional<User> user = userService.activateRegistration(key);
//        if (!user.isPresent()) {
//            throw new AccountResourceException("No user was found for this activation key");
//        }
//    }

    private static class AccountResourceException extends BadRequestAlertException {
        private AccountResourceException(String message) {
            super("", "Account", message);
        }
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= UserDetailsDTO.PASSWORD_MIN_LENGTH &&
            password.length() <= UserDetailsDTO.PASSWORD_MAX_LENGTH;
    }
}
