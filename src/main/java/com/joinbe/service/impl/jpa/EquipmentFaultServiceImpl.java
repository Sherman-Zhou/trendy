package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.DateUtils;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.EquipmentFault;
import com.joinbe.repository.EquipmentFaultRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.security.UserLoginInfo;
import com.joinbe.service.EquipmentFaultService;
import com.joinbe.service.dto.EquipmentFaultDTO;
import com.joinbe.service.dto.EquipmentFaultHandleDTO;
import com.joinbe.web.rest.vm.EquipmentFaultVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link EquipmentFault}.
 */
@Service("JpaEquipmentFaultService")
@Transactional
public class EquipmentFaultServiceImpl implements EquipmentFaultService {

    private final Logger log = LoggerFactory.getLogger(EquipmentFaultServiceImpl.class);

    private final EquipmentFaultRepository equipmentFaultRepository;

    public EquipmentFaultServiceImpl(EquipmentFaultRepository equipmentFaultRepository) {
        this.equipmentFaultRepository = equipmentFaultRepository;
    }

    /**
     * Save a equipmentFault.
     *
     * @param equipmentFaultDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EquipmentFaultDTO save(EquipmentFaultDTO equipmentFaultDTO) {
        log.debug("Request to save EquipmentFault : {}", equipmentFaultDTO);
        EquipmentFault equipmentFault = EquipmentFaultService.toEntity(equipmentFaultDTO);
        equipmentFault = equipmentFaultRepository.save(equipmentFault);
        return EquipmentFaultService.toDto(equipmentFault);
    }

    @Override
    public Optional<EquipmentFaultDTO> read(Long id) {
        return Optional.of(equipmentFaultRepository
            .findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(equipmentFault -> {
                SecurityUtils.checkMerchantPermission(equipmentFault.getEquipment().getMerchant());
                log.debug("isRead: {}", equipmentFault.getIsRead());
                equipmentFault.setIsRead(true);
                return equipmentFault;
            })
            .map(EquipmentFaultService::toDto);
    }

    @Override
    public void batchRead(List<Long> ids) {
       List<EquipmentFault> faults = equipmentFaultRepository.findAllById(ids);
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
       faults.forEach(equipmentFault -> {
           log.debug("isRead:{} of {}", equipmentFault.getIsRead(), equipmentFault.getId());
           SecurityUtils.checkMerchantPermission(loginInfo, equipmentFault.getEquipment().getMerchant());
           equipmentFault.setIsRead(true);
       });
    }

    /**
     * Get all the equipmentFaults.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentFaultDTO> findAll(Pageable pageable, EquipmentFaultVM vm) {
        log.debug("Request to get all EquipmentFaults");
        QueryParams<EquipmentFault> queryParams = new QueryParams<>();
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
//
        queryParams.and("vehicle.merchant.id", Filter.Operator.eq, loginInfo.getMerchantId());


        if (vm.getIsRead() != null) {
            queryParams.and("isRead", Filter.Operator.eq, vm.getIsRead());
        }
//        if (StringUtils.isNotEmpty(vm.getEquipmentIdNum())) {
//            queryParams.and("equipment.identifyNumber", Filter.Operator.like, vm.getEquipmentIdNum());
//        }

        if (vm.getVehicleId() != null) {
            queryParams.and("vehicle.id", Filter.Operator.eq, vm.getVehicleId());
        }

        if (StringUtils.isNotEmpty(vm.getAlertType())) {
            queryParams.and("alertType", Filter.Operator.like, vm.getAlertType());
        }

//        if (StringUtils.isNotEmpty(vm.getAlertContent())) {
//            queryParams.and("alertContent", Filter.Operator.like, vm.getAlertContent());
//        }
        if (StringUtils.isNotEmpty(vm.getStartDate())) {
            Date startDate = DateUtils.parseDate(vm.getStartDate(), DateUtils.PATTERN_DATE);
            queryParams.and("createdDate", Filter.Operator.greaterThanOrEqualTo, startDate);
        }
        if (StringUtils.isNotEmpty(vm.getEndDate())) {
            Date endDate = DateUtils.parseDate(vm.getEndDate() + DateUtils.END_DATE_TIME, DateUtils.PATTERN_DATEALLTIME);
            queryParams.and("createdDate", Filter.Operator.lessThanOrEqualTo, endDate);
        }
        Specification<EquipmentFault> specification = Specification.where(queryParams);
        if (StringUtils.isNotEmpty(vm.getEquipmentIdNum())) {

            Specification<EquipmentFault> itemSpecification = (Specification<EquipmentFault>) (root, criteriaQuery, criteriaBuilder) -> {

                Predicate identifyNumberPredicate = criteriaBuilder.like(root.get("equipment").get("identifyNumber"), "%" + vm.getEquipmentIdNum().trim() + "%");
                Predicate createdByPredicate = criteriaBuilder.like(root.get("alertContent"), "%" + vm.getEquipmentIdNum().trim() + "%");

                return criteriaBuilder.or(identifyNumberPredicate, createdByPredicate);
            };
            specification = specification.and(itemSpecification);
        }

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
            pageable.getSort().and(Sort.by(Sort.Direction.DESC, "createdDate")));

        return equipmentFaultRepository.findAll(specification, pageable)
            .map(EquipmentFaultService::toDto);
    }

    /**
     * Get one equipmentFault by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentFaultDTO> findOne(Long id) {
        log.debug("Request to get EquipmentFault : {}", id);
        return equipmentFaultRepository.findById(id)
            .map(EquipmentFaultService::toDto);
    }

    /**
     * Delete the equipmentFault by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EquipmentFault : {}", id);
        equipmentFaultRepository.deleteById(id);
    }

    @Override
    public Optional<EquipmentFaultDTO> handle(EquipmentFaultHandleDTO equipmentFaultHandleDTO) {
       Optional<EquipmentFault> equipmentFaultOptional =  equipmentFaultRepository.findById(equipmentFaultHandleDTO.getId());
       if(equipmentFaultOptional.isPresent()){
           EquipmentFault equipmentFault = equipmentFaultOptional.get();
           SecurityUtils.checkMerchantPermission(equipmentFault.getEquipment().getMerchant());
           equipmentFault.setRemark(equipmentFaultHandleDTO.getRemark());
           equipmentFault.setHandledOn(Instant.now());
           return Optional.of(EquipmentFaultService.toDto(equipmentFault));
       }
        return Optional.empty();
    }
}
