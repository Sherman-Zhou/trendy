package com.joinbe.service.converter;


import com.joinbe.domain.Division;
import com.joinbe.service.dto.DivisionDTO;


import java.util.ArrayList;
import java.util.List;

 public class DivisionConverter {
    public static List<Division> toEntity(List<DivisionDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Division> list = new ArrayList<Division>( dtoList.size() );
        for ( DivisionDTO divisionDTO : dtoList ) {
            list.add( toEntity( divisionDTO ) );
        }

        return list;
    }

    public static  List<DivisionDTO> toDto(List<Division> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<DivisionDTO> list = new ArrayList<DivisionDTO>( entityList.size() );
        for ( Division division : entityList ) {
            list.add( toDto( division ) );
        }

        return list;
    }

    public static DivisionDTO toDto(Division division) {
        if ( division == null ) {
            return null;
        }

        DivisionDTO divisionDTO = new DivisionDTO();

        divisionDTO.setParentId( divisionParentId( division ) );
        divisionDTO.setId( division.getId() );
        divisionDTO.setName( division.getName() );
        divisionDTO.setDescription( division.getDescription() );
        divisionDTO.setCode( division.getCode() );
        divisionDTO.setStatus( division.getStatus());
        divisionDTO.setCreatedBy( division.getCreatedBy() );
        divisionDTO.setCreatedDate( division.getCreatedDate() );
        divisionDTO.setLastModifiedBy( division.getLastModifiedBy() );
        divisionDTO.setLastModifiedDate( division.getLastModifiedDate() );

        return divisionDTO;
    }


    public static Division toEntity(DivisionDTO divisionDTO) {
        if ( divisionDTO == null ) {
            return null;
        }

        Division division = new Division();

       // division.setParent( fromId( divisionDTO.getParentId() ) );
        division.setId( divisionDTO.getId() );
        division.setName( divisionDTO.getName() );
        division.setDescription( divisionDTO.getDescription() );
        division.setCode( divisionDTO.getCode() );
        division.setStatus( divisionDTO.getStatus() );
        division.setCreatedBy( divisionDTO.getCreatedBy() );
        division.setCreatedDate( divisionDTO.getCreatedDate() );
        division.setLastModifiedBy( divisionDTO.getLastModifiedBy() );
        division.setLastModifiedDate( divisionDTO.getLastModifiedDate() );

        return division;
    }

    private static Long divisionParentId(Division division) {
        if ( division == null ) {
            return null;
        }
        Division parent = division.getParent();
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
