package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.DictEntry;
import com.joinbe.service.dto.DictEntryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.DictEntry}.
 */
public interface DictEntryService {

    /**
     * Save a dictEntry.
     *
     * @param dictEntryDTO the entity to save.
     * @return the persisted entity.
     */
    DictEntryDTO save(DictEntryDTO dictEntryDTO);

    /**
     * Get all the dictEntries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DictEntryDTO> findAll(Pageable pageable);

    /**
     * Get the "id" dictEntry.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DictEntryDTO> findOne(Long id);

    /**
     * Delete the "id" dictEntry.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    default DictEntryDTO toDto(DictEntry model) {
        DictEntryDTO dto = BeanConverter.toDto(model, DictEntryDTO.class);
        return dto;
    }

    default DictEntry toEntity(DictEntryDTO dto) {
        DictEntry model = BeanConverter.toEntity(dto, DictEntry.class);
        return model;
    }
}
