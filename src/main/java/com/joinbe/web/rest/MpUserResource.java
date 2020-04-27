package com.joinbe.web.rest;

import com.joinbe.domain.User;
import com.joinbe.service.UserService;
import com.joinbe.service.dto.UserDTO;
import com.joinbe.service.dto.UserDetailsDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.errors.EmailAlreadyUsedException;
import com.joinbe.web.rest.errors.LoginAlreadyUsedException;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
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

@RestController
@RequestMapping("/api/mp")
public class MpUserResource {
    private final Logger log = LoggerFactory.getLogger(MpUserResource.class);
    private final UserService userService;

    public MpUserResource(@Qualifier("mpUserService") UserService userService) {
        this.userService = userService;
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

    @PostMapping("/users")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDetailsDTO userDTO) throws URISyntaxException {

        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userService.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (userService.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            User newUser = userService.createUser(userDTO);

            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .body(newUser);
        }
    }
}
