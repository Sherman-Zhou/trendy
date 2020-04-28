package com.joinbe.web.rest;

import com.joinbe.config.Constants;
import com.joinbe.domain.User;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.service.DivisionService;
import com.joinbe.service.MailService;
import com.joinbe.service.UserService;
import com.joinbe.service.dto.DivisionDTO;
import com.joinbe.service.dto.RoleDTO;
import com.joinbe.service.dto.UserDTO;
import com.joinbe.service.dto.UserDetailsDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.errors.EmailAlreadyUsedException;
import com.joinbe.web.rest.errors.LoginAlreadyUsedException;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import com.joinbe.web.rest.vm.UserOperation;
import com.joinbe.web.rest.vm.UserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link User} entity, and needs to fetch its collection of authorities.
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
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);


    private final UserService userService;

    private final DivisionService divisionService;

    private final MailService mailService;

    public UserResource(@Qualifier("JpaUserService") UserService userService, MailService mailService, DivisionService divisionService) {
        this.userService = userService;
        this.mailService = mailService;
        this.divisionService = divisionService;
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
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userService.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (userService.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            User newUser = userService.createUser(userDTO);
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .body(newUser);
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
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        log.debug("REST request to update User : {}", userDTO);
        Optional<User> existingUser = userService.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = userService.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }
        Optional<UserDTO> updatedUser = userService.updateUser(userDTO);

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
    public ResponseEntity<UserDTO> disableOrEnableUser(@PathVariable Long id, @PathVariable UserOperation op) {
        log.debug("REST request to {} User : {} ", op, id);
        RecordStatus status = UserOperation.enable.equals(op) ? RecordStatus.ACTIVE : RecordStatus.INACTIVE;
        Optional<UserDTO> updatedUser = userService.updateUserStatus(id, status);
        return ResponseUtil.wrapOrNotFound(updatedUser);
    }

    /**
     * {@code GET   /user/:id/reset} : Send an email to reset the password of the user.
     *
     * @param id the id of the user.
     */
    @GetMapping(path = "/user/{id}/reset")
    public ResponseEntity<UserDTO> requestPasswordReset(@PathVariable Long id) {
        Optional<User> user = userService.requestPasswordReset(id);
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
    public ResponseEntity<PageData<UserDTO>> getAllUsers(Pageable pageable, UserVM userVM) {
        final Page<UserDTO> page = userService.getAllManagedUsers(pageable, userVM);
        return ResponseUtil.toPageData(page);
    }

    /**
     * Gets a list of all roles.
     *
     * @return a list of all roles.
     */
    @GetMapping("/users/roles")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<RoleDTO> getRoles() {
        return userService.getRoles();
    }


    /**
     * {@code PUT  /users/{userId}/assign} : assign division to role.
     *
     * @param userId     the id of the user to update.
     * @param divisionId the id  of division to assign.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userDto,
     * or with status {@code 500 (Internal Server Error)} if the userDto couldn't be updated.
     */
    @PutMapping("/users/{userId}/assign")
    public ResponseEntity<UserDTO> assignDivision(@PathVariable Long userId, @Valid @RequestBody Long divisionId) {
        log.debug("REST request to assign division: {} to user : {}", divisionId, userId);
        UserDTO result = userService.assignDivision(userId, divisionId);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * GET  /users/division/:parentId/children: get all the children departments.
     *
     * @param parentId the parentId
     * @return the ResponseEntity with status 200 (OK) and the list of divisions in body
     */
    @GetMapping("/users/division/:parentId/children")
    public List<DivisionDTO> getAllSubDivisions(@PathVariable Long parentId) {
        log.debug("REST request to get a List of children divisions for parent:{}", parentId);
        return divisionService.findAllByParentId(parentId);
    }

    /**
     * {@code GET /users/:login} : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    public ResponseEntity<UserDetailsDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return ResponseUtil.wrapOrNotFound(
            userService.getUserWithAuthoritiesByLogin(login)
                .map(UserDetailsDTO::new));
    }

//    /**
//     * {@code DELETE /users/:login} : delete the "login" User.
//     *
//     * @param login the login of the user to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
////    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
//    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
//        log.debug("REST request to delete User: {}", login);
//        userService.deleteUser(login);
//        return ResponseEntity.noContent().build();
//    }

    /**
     * {@code DELETE /users/:id} : delete the  User by Id.
     *
     * @param id the id of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{id}")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("REST request to delete User: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
