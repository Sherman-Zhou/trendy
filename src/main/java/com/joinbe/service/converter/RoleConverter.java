package com.joinbe.service.converter;


import com.joinbe.domain.Permission;
import com.joinbe.domain.Role;
import com.joinbe.service.dto.PermissionDTO;
import com.joinbe.service.dto.RoleDTO;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


 public class RoleConverter {

    public static RoleDTO toDto(Role entity) {
        if ( entity == null ) {
            return null;
        }

        RoleDTO roleDTO = new RoleDTO();

        roleDTO.setId( entity.getId() );
        roleDTO.setName( entity.getName() );
        roleDTO.setDescription( entity.getDescription() );
        roleDTO.setCode( entity.getCode() );
        roleDTO.setStatus( entity.getStatus() );

        roleDTO.setPermissions( permissionSetToPermissionDTOSet( entity.getPermissions() ) );

        return roleDTO;
    }

    public static List<Role> toEntity(List<RoleDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Role> list = new ArrayList<Role>( dtoList.size() );
        for ( RoleDTO roleDTO : dtoList ) {
            list.add( toEntity( roleDTO ) );
        }

        return list;
    }

    public static List<RoleDTO> toDto(List<Role> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<RoleDTO> list = new ArrayList<RoleDTO>( entityList.size() );
        for ( Role role : entityList ) {
            list.add( toDto( role ) );
        }

        return list;
    }


    public static Role toEntity(RoleDTO roleDTO) {
        if ( roleDTO == null ) {
            return null;
        }

        Role role = new Role();

        role.setId( roleDTO.getId() );
        role.setName( roleDTO.getName() );
        role.setDescription( roleDTO.getDescription() );
        role.setCode( roleDTO.getCode() );
        role.setStatus( roleDTO.getStatus() );

        role.setPermissions( permissionDTOSetToPermissionSet( roleDTO.getPermissions() ) );

        return role;
    }

    protected static Set<PermissionDTO> permissionSetToPermissionDTOSet(Set<Permission> set) {
        if ( set == null ) {
            return null;
        }

        Set<PermissionDTO> set1 = new HashSet<PermissionDTO>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Permission permission : set ) {
            set1.add( PermissionConverter.toDto( permission ) );
        }

        return set1;
    }

    protected static Set<Permission> permissionDTOSetToPermissionSet(Set<PermissionDTO> set) {
        if ( set == null ) {
            return null;
        }

        Set<Permission> set1 = new HashSet<Permission>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( PermissionDTO permissionDTO : set ) {
            set1.add( PermissionConverter.toEntity( permissionDTO ) );
        }

        return set1;
    }
}
