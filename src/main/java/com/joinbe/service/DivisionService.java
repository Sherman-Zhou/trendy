package com.joinbe.service;

import com.joinbe.service.dto.DivisionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    Page<DivisionDTO> findAll(Pageable pageable);

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
}
