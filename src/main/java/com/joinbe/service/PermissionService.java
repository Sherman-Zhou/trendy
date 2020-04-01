package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.Permission;
import com.joinbe.service.dto.PermissionDTO;
import com.joinbe.web.rest.vm.PermissionVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.Permission}.
 */
public interface PermissionService {

    /**
     * Save a permission.
     *
     * @param permissionDTO the entity to save.
     * @return the persisted entity.
     */
    PermissionDTO save(PermissionDTO permissionDTO);

    /**
     * Get all the permissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PermissionDTO> findAll(Pageable pageable, PermissionVM vm);

    /**
     * Get the "id" permission.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PermissionDTO> findOne(Long id);

    /**
     * Delete the "id" permission.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<Permission> loadAllPermissions();


    default PermissionDTO toDto(Permission permission) {
        PermissionDTO dto = BeanConverter.toDto(permission, PermissionDTO.class);
        dto.setParentId(permission.getParent() != null ? permission.getParent().getId() : null);
        return dto;
    }

    default Permission toEntity(PermissionDTO dto) {
        Permission entity = BeanConverter.toEntity(dto, Permission.class);
        return entity;
    }
}
