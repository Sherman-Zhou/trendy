package com.joinbe.service.converter;


import com.joinbe.domain.Permission;
import com.joinbe.service.dto.PermissionDTO;


import java.util.ArrayList;
import java.util.List;


public class PermissionConverter {


    public static List<Permission> toEntity(List<PermissionDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Permission> list = new ArrayList<Permission>( dtoList.size() );
        for ( PermissionDTO permissionDTO : dtoList ) {
            list.add( toEntity( permissionDTO ) );
        }

        return list;
    }

    public static List<PermissionDTO> toDto(List<Permission> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PermissionDTO> list = new ArrayList<PermissionDTO>( entityList.size() );
        for ( Permission permission : entityList ) {
            list.add( toDto( permission ) );
        }

        return list;
    }


    public static PermissionDTO toDto(Permission permission) {
        if ( permission == null ) {
            return null;
        }

        PermissionDTO permissionDTO = new PermissionDTO();

        permissionDTO.setParentId( permissionParentId( permission ) );
        permissionDTO.setId( permission.getId() );
        permissionDTO.setName( permission.getName() );
        permissionDTO.setLvl( permission.getLvl() );
        permissionDTO.setPermissionType( permission.getPermissionType() );
        permissionDTO.setTitle( permission.getTitle() );
        permissionDTO.setFrontendPath( permission.getFrontendPath() );
        permissionDTO.setIcon( permission.getIcon() );
        permissionDTO.setOperationType( permission.getOperationType() );
        permissionDTO.setDescription( permission.getDescription() );
        permissionDTO.setSortOrder( permission.getSortOrder() );
        permissionDTO.setBackendUrl( permission.getBackendUrl() );
        permissionDTO.setStatus( permission.getStatus() );

        return permissionDTO;
    }


    public static Permission toEntity(PermissionDTO permissionDTO) {
        if ( permissionDTO == null ) {
            return null;
        }

        Permission permission = new Permission();

        //permission.setParent( fromId( permissionDTO.getParentId() ) );
        permission.setId( permissionDTO.getId() );
        permission.setName( permissionDTO.getName() );
        permission.setLvl( permissionDTO.getLvl() );
        permission.setPermissionType( permissionDTO.getPermissionType() );
        permission.setTitle( permissionDTO.getTitle() );
        permission.setFrontendPath( permissionDTO.getFrontendPath() );
        permission.setIcon( permissionDTO.getIcon() );
        permission.setOperationType( permissionDTO.getOperationType() );
        permission.setDescription( permissionDTO.getDescription() );
        permission.setSortOrder( permissionDTO.getSortOrder() );
        permission.setBackendUrl( permissionDTO.getBackendUrl() );
        permission.setStatus( permissionDTO.getStatus() );

        return permission;
    }

    private static Long permissionParentId(Permission permission) {
        if ( permission == null ) {
            return null;
        }
        Permission parent = permission.getParent();
        if ( parent == null ) {
            return null;
        }
        Long id = parent.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
