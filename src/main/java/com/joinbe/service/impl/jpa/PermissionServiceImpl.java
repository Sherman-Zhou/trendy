package com.joinbe.service.impl.jpa;

import com.joinbe.domain.Permission;
import com.joinbe.domain.enumeration.PermissionType;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.PermissionRepository;
import com.joinbe.repository.RoleRepository;
import com.joinbe.service.PermissionService;
import com.joinbe.service.dto.PermissionDTO;
import com.joinbe.service.dto.PermissionSummaryDTO;
import com.joinbe.web.rest.vm.PermissionVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Permission}.
 */
@Service("JpaPermissionService")
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);

    private final PermissionRepository permissionRepository;

    private final RoleRepository roleRepository;


    public PermissionServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Save a permission.
     *
     * @param permissionDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public PermissionDTO save(PermissionDTO permissionDTO) {
        log.debug("Request to save Permission : {}", permissionDTO);
        Permission permission = this.toEntity(permissionDTO);
        permission = permissionRepository.save(permission);
        return this.toDto(permission);
    }

    /**
     * Get all the permissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PermissionDTO> findAll(Pageable pageable, PermissionVM vm) {
        log.debug("Request to get all Permissions");
        return permissionRepository.findAll(pageable)
            .map(this::toDto);
    }

    /**
     * Get one permission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PermissionDTO> findOne(Long id) {
        log.debug("Request to get Permission : {}", id);
        return permissionRepository.findById(id)
            .map(this::toDto);
    }

    /**
     * Delete the permission by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Permission : {}", id);
        permissionRepository.deleteById(id);
    }

    @Override
    public List<Permission> loadAllPermissions() {
        List<Permission> permissions = permissionRepository.findAllByStatus(RecordStatus.ACTIVE);

        return permissions.stream().filter(permission -> !PermissionType.FOLDER.equals(permission.getPermissionType()))
            .collect(Collectors.toList());
    }

    @Override
    public List<PermissionSummaryDTO> findAllActivePerms(Long roleId) {
        log.debug("Request to get all active permissions");
        List<Permission> allActivePerms = permissionRepository.findAllPermsByStatusOrderBySortOrder(RecordStatus.ACTIVE);
        final List<Long> permIdsInRole = roleId != null ? getPermissionIdsInRole(roleId) : new ArrayList<>();
        //group by parentId
        List<PermissionSummaryDTO> childPerms = allActivePerms.stream()
            .filter(permission -> permission.getParent() != null)
            .sorted(Comparator.comparing(Permission::getSortOrder))
            .map(PermissionSummaryDTO::new)
            .map(summaryDTO -> {
                if (isPermissionInRole(permIdsInRole, summaryDTO.getId())) {
                    log.debug("contains");
                    summaryDTO.setChecked(true);
                }
                summaryDTO.setExpand(true);
                return summaryDTO;
            }).collect(Collectors.toList());

        Map<Long, List<PermissionSummaryDTO>> childMenusMap = childPerms.stream().collect(Collectors.groupingBy(PermissionSummaryDTO::getParentId));
        //establish relationship for child menu
        for (PermissionSummaryDTO permissionDTO : childPerms) {
            if (!CollectionUtils.isEmpty(childMenusMap.get(permissionDTO.getId()))) {
                permissionDTO.setChildren(childMenusMap.get(permissionDTO.getId()));
                permissionDTO.setExpand(true);
            }
        }

        //get Root Menus
        List<PermissionSummaryDTO> rootMenus = allActivePerms.stream()
            .filter(menu -> menu.getParent() == null)
            .map(PermissionSummaryDTO::new).map(permissionDTO -> {
                permissionDTO.setChildren(childMenusMap.get(permissionDTO.getId()));
                if (isPermissionInRole(permIdsInRole, permissionDTO.getId())) {
                    permissionDTO.setChecked(true);
                }
                permissionDTO.setExpand(true);
                return permissionDTO;
            })
            .collect(Collectors.toList());

        return rootMenus;
    }

    @Override
    public List<Long> getRolePermIds(Long roleId) {
        return getPermissionIdsInRole(roleId);
    }

    private boolean isPermissionInRole(List<Long> permIdsInRole, Long permissionId) {
        //log.debug("permIdsInRole: {}: permissionId={}", permIdsInRole, permissionId);
        return permIdsInRole.contains(permissionId);
    }

    private List<Long> getPermissionIdsInRole(Long id) {
        return roleRepository.findRoleWithPermissionsById(id).stream().map(role -> {
            log.debug("Permissions {} for {}", role.getPermissions(), role.getId());
            return role.getPermissions();
        })
            .flatMap(permissions -> {
                log.debug("permissions: {} ", permissions);
                return permissions.stream();
            })
            .map(Permission::getId)
            .sorted( )
            .collect(Collectors.toList());
    }

}
