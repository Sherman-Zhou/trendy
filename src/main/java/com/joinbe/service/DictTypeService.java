package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.DictType;
import com.joinbe.service.dto.DictTypeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.DictType}.
 */
public interface DictTypeService {

    /**
     * Save a dictType.
     *
     * @param dictTypeDTO the entity to save.
     * @return the persisted entity.
     */
    DictTypeDTO save(DictTypeDTO dictTypeDTO);

    /**
     * Get all the dictTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DictTypeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" dictType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DictTypeDTO> findOne(Long id);

    /**
     * Delete the "id" dictType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    default DictTypeDTO toDto(DictType model) {
        DictTypeDTO dto = BeanConverter.toDto(model, DictTypeDTO.class);
        return dto;
    }

    default DictType toEntity(DictTypeDTO dto) {
        DictType model = BeanConverter.toEntity(dto, DictType.class);
        return model;
    }
}
