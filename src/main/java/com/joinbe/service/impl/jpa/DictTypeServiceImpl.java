package com.joinbe.service.impl.jpa;

import com.joinbe.domain.DictType;
import com.joinbe.repository.DictTypeRepository;
import com.joinbe.service.DictTypeService;
import com.joinbe.service.dto.DictTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link DictType}.
 */
@Service("JpaDictTypeService")
@Transactional
public class DictTypeServiceImpl implements DictTypeService {

    private final Logger log = LoggerFactory.getLogger(DictTypeServiceImpl.class);

    private final DictTypeRepository dictTypeRepository;


    public DictTypeServiceImpl(DictTypeRepository dictTypeRepository) {
        this.dictTypeRepository = dictTypeRepository;
    }

    /**
     * Save a dictType.
     *
     * @param dictTypeDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public DictTypeDTO save(DictTypeDTO dictTypeDTO) {
        log.debug("Request to save DictType : {}", dictTypeDTO);
        DictType dictType = this.toEntity(dictTypeDTO);
        dictType = dictTypeRepository.save(dictType);
        return this.toDto(dictType);
    }

    /**
     * Get all the dictTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DictTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all DictTypes");
        return dictTypeRepository.findAll(pageable)
            .map(this::toDto);
    }

    /**
     * Get one dictType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<DictTypeDTO> findOne(Long id) {
        log.debug("Request to get DictType : {}", id);
        return dictTypeRepository.findById(id)
            .map(this::toDto);
    }

    /**
     * Delete the dictType by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete DictType : {}", id);
        dictTypeRepository.deleteById(id);
    }
}
