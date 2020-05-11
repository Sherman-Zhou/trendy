package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.Role;
import com.joinbe.service.dto.RoleDTO;
import com.joinbe.service.dto.RoleDetailsDTO;
import com.joinbe.web.rest.vm.RoleVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.Role}.
 */
public interface RoleService {

    /**
     * Save a role.
     *
     * @param roleDTO the entity to save.
     * @return the persisted entity.
     */
    RoleDTO save(RoleDTO roleDTO);

    RoleDetailsDTO assignPermission(Long roleId, List<Long> permissionIds);


    /**
     * Get all the roles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RoleDTO> findAll(Pageable pageable, RoleVM roleVm);

    /**
     * Get the "id" role.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RoleDetailsDTO> findOne(Long id);

    /**
     * Delete the "id" role.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    static RoleDTO toDto(Role role) {

        return BeanConverter.toDto(role, RoleDTO.class);
    }

    static RoleDetailsDTO toDetailDto(Role role) {
        return BeanConverter.toDto(role, RoleDetailsDTO.class);
    }

    static Role toEntity(RoleDTO roleDTO) {

        return BeanConverter.toEntity(roleDTO, Role.class);
    }
}
