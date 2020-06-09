package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Division;
import com.joinbe.domain.Permission;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.DivisionRepository;
import com.joinbe.service.DivisionService;
import com.joinbe.service.dto.DivisionDTO;
import com.joinbe.service.dto.PermissionSummaryDTO;
import com.joinbe.web.rest.vm.DivisionVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Division}.
 */
@Service("JpaDivisionService")
@Transactional
public class DivisionServiceImpl implements DivisionService {

    private final Logger log = LoggerFactory.getLogger(DivisionServiceImpl.class);

    private final DivisionRepository divisionRepository;


    public DivisionServiceImpl(DivisionRepository divisionRepository) {
        this.divisionRepository = divisionRepository;
    }

    /**
     * Save a division.
     *
     * @param divisionDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public DivisionDTO save(DivisionDTO divisionDTO) {
        log.debug("Request to save Division : {}", divisionDTO);
        Division division = DivisionService.toEntity(divisionDTO);
        division = divisionRepository.save(division);
        divisionRepository.flush();
        return DivisionService.toDto(division);
    }

    /**
     * Get all the divisions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DivisionDTO> findAll(Pageable pageable, DivisionVM vm) {
        log.debug("Request to get all Divisions");
        QueryParams<Division> queryParams = new QueryParams<>();

        if (StringUtils.isNotEmpty(vm.getCode())) {
            queryParams.and("code", Filter.Operator.eq, vm.getCode());
        }
        if (StringUtils.isNotEmpty(vm.getName())) {
            queryParams.and("name", Filter.Operator.like, vm.getName());
        }
        if (StringUtils.isNotEmpty(vm.getDescription())) {
            queryParams.and("description", Filter.Operator.like, vm.getDescription());
        }
        if (StringUtils.isNotEmpty(vm.getStatus())) {
            queryParams.and("status", Filter.Operator.eq, RecordStatus.resolve(vm.getStatus()));
        }
        return divisionRepository.findAll(queryParams, pageable)
            .map(DivisionService::toDto);
    }

    /**
     * Get one division by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<DivisionDTO> findOne(Long id) {
        log.debug("Request to get Division : {}", id);
        return divisionRepository.findById(id)
            .map(DivisionService::toDto);
    }

    /**
     * Delete the division by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Division : {}", id);
        divisionRepository.deleteById(id);
    }

    @Override
    public List<DivisionDTO> findAllActiveDivisions() {
        List<DivisionDTO> divisions = divisionRepository.findAll().stream()
                .filter(division -> RecordStatus.ACTIVE.equals(division.getStatus()))
                .map(DivisionService::toDto)
                .collect(Collectors.toList());;


        //group by parentId

       List<DivisionDTO> children = divisions.stream()
           // .filter(division -> RecordStatus.ACTIVE.equals(division.getStatus()))
            .filter(division -> division.getParentId() != null)
            .sorted(Comparator.comparing(DivisionDTO::getName))
            .collect(Collectors.toList());


        Map<Long, List<DivisionDTO>> childMenusMap = children.stream().collect(Collectors.groupingBy(DivisionDTO::getParentId));
        //establish relationship for child menu
        for (DivisionDTO divisionDTO : divisions) {
            if (!CollectionUtils.isEmpty(childMenusMap.get(divisionDTO.getId()))) {
                divisionDTO.setChildren(childMenusMap.get(divisionDTO.getId()));
//                permissionDTO.setExpand(true);
            }
        }

        //get Root Menus
        List<DivisionDTO> rootDivision = divisions.stream()
            .filter(menu -> menu.getParentId() == null)
            .collect(Collectors.toList());

        return rootDivision;
    }

    @Override
    public List<DivisionDTO> findUserDivision(String login) {
        List<Division> divisions = divisionRepository.findAllByUserLogin(login);
        return divisions.stream().map(DivisionService::toDto).collect(Collectors.toList());
    }
}
