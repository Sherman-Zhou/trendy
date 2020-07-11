package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Permission;
import com.joinbe.domain.Role;
import com.joinbe.domain.Staff;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.RoleRepository;
import com.joinbe.repository.StaffRepository;
import com.joinbe.service.RoleService;
import com.joinbe.service.dto.RoleDTO;
import com.joinbe.service.dto.RoleDetailsDTO;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.RoleVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Role}.
 */
@Service("JpaRoleService")
@Transactional
public class RoleServiceImpl implements RoleService {

    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;

    private final StaffRepository staffRepository;


    public RoleServiceImpl(RoleRepository roleRepository, StaffRepository staffRepository) {
        this.roleRepository = roleRepository;
        this.staffRepository = staffRepository;
    }

    /**
     * Save a role.
     *
     * @param roleDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public RoleDTO save(RoleDTO roleDTO) {
        log.debug("Request to save Role : {}", roleDTO);
        Role role;
        if(roleDTO.getId()!=null) {
             role = roleRepository.getOne(roleDTO.getId());
             role.setStatus(RecordStatus.resolve(roleDTO.getStatus()));
             role.setCode(roleDTO.getCode());
             role.setDescription(roleDTO.getDescription());
             role.setName(role.getName());
        }else {
              role = RoleService.toEntity(roleDTO);
        }

        role = roleRepository.save(role);
        return RoleService.toDto(role);
    }

    @Override
    public Optional<Role> findOneByCode(String code) {
        return roleRepository.findByCodeAndStatusNot(code, RecordStatus.DELETED);
    }

    @Override
    public Optional<Role> findOneByName(String name) {
        return roleRepository.findByNameAndStatusNot(name, RecordStatus.DELETED);
    }

    @Override
    public RoleDetailsDTO assignPermission(Long roleId, List<Long> permissionIds) {
        RoleDetailsDTO roleDTO;
        Optional<Role> roleOptional = roleRepository.findById(roleId);

        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            role.getPermissions().clear();
            for (Long permissionId : permissionIds) {
                Permission permission = new Permission();
                permission.setId(permissionId);
                role.getPermissions().add(permission);
            }
            roleDTO = RoleService.toDetailDto(role);
        } else {
            throw new BadRequestAlertException("Invalid id", "Role", "idnull");
        }

        return roleDTO;
    }

    /**
     * Get all the roles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RoleDTO> findAll(Pageable pageable, RoleVM vm) {
        log.debug("Request to get all Roles");
        QueryParams<Role> queryParams = new QueryParams<>();

        if (StringUtils.isNotEmpty(vm.getCode())) {
            queryParams.and("code", Filter.Operator.eq, vm.getCode());
        }
        if (StringUtils.isNotEmpty(vm.getName())) {
            queryParams.and("name", Filter.Operator.like, vm.getName());
        }
        if (StringUtils.isNotEmpty(vm.getDescription())) {
            queryParams.and("description", Filter.Operator.like, vm.getDescription());
        }
        if (StringUtils.isNotEmpty(vm.getStatus())) {
            queryParams.and("status", Filter.Operator.eq, RecordStatus.resolve(vm.getStatus()));
        }
        return roleRepository.findAll(queryParams, pageable)
            .map(RoleService::toDto);
    }


    /**
     * Get one role by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RoleDetailsDTO> findOne(Long id) {
        log.debug("Request to get Role : {}", id);
        return roleRepository.findOneWithEagerRelationships(id)
            .map(RoleService::toDetailDto);
    }

    /**
     * Delete the role by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Role : {}", id);
        List<Staff> staff = staffRepository.findUsersByRoleId(id);
        if (!staff.isEmpty()) {
            throw new BadRequestAlertException("Role is in use", "Role", "inuse");
        }
        Role role = roleRepository.getOne(id);
        role.setStatus(RecordStatus.DELETED);
        // roleRepository.deleteById(id);
    }
}
