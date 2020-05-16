package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.Division;
import com.joinbe.service.dto.DivisionDTO;
import com.joinbe.web.rest.vm.DivisionVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.Division}.
 */
public interface DivisionService {

    /**
     * Save a division.
     *
     * @param divisionDTO the entity to save.
     * @return the persisted entity.
     */
    DivisionDTO save(DivisionDTO divisionDTO);

    /**
     * Get all the divisions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DivisionDTO> findAll(Pageable pageable, DivisionVM vm);

    /**
     * Get the "id" division.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DivisionDTO> findOne(Long id);

    /**
     * Delete the "id" division.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<DivisionDTO> findAllByParentId(Long parentId);

    static DivisionDTO toDto(Division division) {
        DivisionDTO dto = BeanConverter.toDto(division, DivisionDTO.class);
        // dto.setParentId(division.getParentId());
        dto.setHasChildren(!division.getChildren().isEmpty());
        return dto;
    }

    static Division toEntity(DivisionDTO divisionDTO) {

        Division division = BeanConverter.toEntity(divisionDTO, Division.class);
        if (divisionDTO.getParentId() != null) {
            Division parent = new Division();
            parent.setId(divisionDTO.getParentId());

            division.setParent(parent);
        }
        return division;
    }
}
