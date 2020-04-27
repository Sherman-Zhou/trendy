package com.joinbe.web.rest;

import com.joinbe.service.PermissionService;
import com.joinbe.service.RoleService;
import com.joinbe.service.dto.PermissionSummaryDTO;
import com.joinbe.service.dto.RoleDTO;
import com.joinbe.service.dto.RoleDetailsDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.PageData;
import com.joinbe.web.rest.vm.ResponseUtil;
import com.joinbe.web.rest.vm.RoleVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing {@link com.joinbe.domain.Role}.
 */
@RestController
@RequestMapping("/api")
public class RoleResource {

    private final Logger log = LoggerFactory.getLogger(RoleResource.class);

    private static final String ENTITY_NAME = "role";


    private final RoleService roleService;

    private final PermissionService permissionService;

    public RoleResource(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    /**
     * {@code POST  /roles} : Create a new role.
     *
     * @param roleDTO the roleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roleDTO, or with status {@code 400 (Bad Request)} if the role has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/roles")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) throws URISyntaxException {
        log.debug("REST request to save Role : {}", roleDTO);
        if (roleDTO.getId() != null) {
            throw new BadRequestAlertException("A new role cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RoleDTO result = roleService.save(roleDTO);
        return ResponseEntity.created(new URI("/api/roles/" + result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /roles} : Updates an existing role.
     *
     * @param roleDTO the roleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roleDTO,
     * or with status {@code 400 (Bad Request)} if the roleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/roles")
    public ResponseEntity<RoleDTO> updateRole(@Valid @RequestBody RoleDTO roleDTO) throws URISyntaxException {
        log.debug("REST request to update Role : {}", roleDTO);
        if (roleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RoleDTO result = roleService.save(roleDTO);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * {@code GET  /roles} : get all the roles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of roles in body.
     */
    @GetMapping("/roles")
    public ResponseEntity<PageData<RoleDTO>> getAllRoles(Pageable pageable, RoleVM roleVM) {
        log.debug("REST request to get a page of Roles");
        Page<RoleDTO> page;
        page = roleService.findAll(pageable, roleVM);

        return ResponseUtil.toPageData(page);
    }

    /**
     * {@code GET  /roles/:id} : get the "id" role.
     *
     * @param id the id of the roleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleDetailsDTO> getRole(@PathVariable Long id) {
        log.debug("REST request to get Role : {}", id);
        Optional<RoleDetailsDTO> roleDTO = roleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(roleDTO);
    }

    /**
     * {@code DELETE  /roles/:id} : delete the "id" role.
     *
     * @param id the id of the roleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        log.debug("REST request to delete Role : {}", id);
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET  /roles/active-perms/:roleId: get all the active permission and set the checked of permission based on roleId.
     *
     * @param roleId the id of the roleDTO to set the initial status of menus.
     * @return the ResponseEntity with status 200 (OK) and the list of Permissions in body
     */
    @GetMapping("/roles/active-perms/{roleId}")
    public List<PermissionSummaryDTO> getAllActivePerms(@PathVariable Long roleId) {
        log.debug("REST request to get all active perms, roleId = {} ", roleId);
        return permissionService.findAllActivePerms(roleId);
    }

    /**
     * GET  /roles/active-perms: get all the active perms.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of Permissions in body
     */
    @GetMapping("/roles/active-perms")
    public List<PermissionSummaryDTO> getAllActivePerms() {
        log.debug("REST request to get all active perms ");
        return permissionService.findAllActivePerms(null);
    }

    /**
     * {@code PUT  /roles/{roleId}/assign} : assign permission to role.
     *
     * @param roleId        the roleDTO to update.
     * @param permissionIds the ids of permissions to assign.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roleDTO,
     * or with status {@code 400 (Bad Request)} if the roleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roleDTO couldn't be updated.
     */
    @PutMapping("/roles/{roleId}/assign")
    public ResponseEntity<RoleDTO> assignPermission(@PathVariable Long roleId, @Valid @RequestBody List<Long> permissionIds) {
        log.debug("REST request to assign permission: {} to role : {}", permissionIds, roleId);

        RoleDTO result = roleService.assignPermission(roleId, permissionIds);
        return ResponseEntity.ok()
            .body(result);
    }

}
