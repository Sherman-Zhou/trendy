package com.joinbe.web.rest;

import com.joinbe.service.UserService;
import com.joinbe.service.dto.UserDTO;
import com.joinbe.web.rest.vm.ManagedUserVM;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mp")
public class MpUserResource {

    @Autowired
    @Qualifier("mpUserService")
    private UserService userService;

    /**
     * {@code GET /users} : get all users.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public ResponseEntity<PageData<UserDTO>> getAllUsers(Pageable pageable, ManagedUserVM userVM) {
        final Page<UserDTO> page = userService.getAllManagedUsers(pageable, userVM);
        return ResponseUtil.toPageData(page);
    }
}
