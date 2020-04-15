package com.joinbe.service.impl.jpa;

import com.joinbe.domain.DictEntry;
import com.joinbe.repository.DictEntryRepository;
import com.joinbe.service.DictEntryService;
import com.joinbe.service.dto.DictEntryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link DictEntry}.
 */
@Service("JpaDictEntryService")
@Transactional
public class DictEntryServiceImpl implements DictEntryService {

    private final Logger log = LoggerFactory.getLogger(DictEntryServiceImpl.class);

    private final DictEntryRepository dictEntryRepository;

    public DictEntryServiceImpl(DictEntryRepository dictEntryRepository) {
        this.dictEntryRepository = dictEntryRepository;
    }

    /**
     * Save a dictEntry.
     *
     * @param dictEntryDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public DictEntryDTO save(DictEntryDTO dictEntryDTO) {
        log.debug("Request to save DictEntry : {}", dictEntryDTO);
        DictEntry dictEntry = this.toEntity(dictEntryDTO);
        dictEntry = dictEntryRepository.save(dictEntry);
        return this.toDto(dictEntry);
    }

    /**
     * Get all the dictEntries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DictEntryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all DictEntries");
        return dictEntryRepository.findAll(pageable)
            .map(this::toDto);
    }

    /**
     * Get one dictEntry by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<DictEntryDTO> findOne(Long id) {
        log.debug("Request to get DictEntry : {}", id);
        return dictEntryRepository.findById(id)
            .map(this::toDto);
    }

    /**
     * Delete the dictEntry by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete DictEntry : {}", id);
        dictEntryRepository.deleteById(id);
    }
}
