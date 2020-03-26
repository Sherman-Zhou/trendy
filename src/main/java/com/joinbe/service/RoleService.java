package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.Role;
import com.joinbe.service.dto.RoleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    /**
     * Get all the roles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RoleDTO> findAll(Pageable pageable);

    /**
     * Get all the roles with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Page<RoleDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" role.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RoleDTO> findOne(Long id);

    /**
     * Delete the "id" role.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    default  RoleDTO toDto(Role role) {
        RoleDTO dto = BeanConverter.toDto(role, RoleDTO.class);

        return dto;
    }

    default Role toEntity(RoleDTO roleDTO) {

        Role role = BeanConverter.toEntity(roleDTO, Role.class);

        return role;
    }
}
