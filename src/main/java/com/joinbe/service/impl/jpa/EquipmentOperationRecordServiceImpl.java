package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.DateUtils;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.EquipmentOperationRecord;
import com.joinbe.repository.EquipmentOperationRecordRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.security.UserLoginInfo;
import com.joinbe.service.EquipmentOperationRecordService;
import com.joinbe.service.dto.EquipmentOperationRecordDTO;
import com.joinbe.web.rest.vm.EquipmentOpRecordVM;
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

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.Optional;

/**
 * Service Implementation for managing {@link EquipmentOperationRecord}.
 */
@Service("JpaEquipmentOperationRecordService")
@Transactional
public class EquipmentOperationRecordServiceImpl implements EquipmentOperationRecordService {

    private final Logger log = LoggerFactory.getLogger(EquipmentOperationRecordServiceImpl.class);

    private final EquipmentOperationRecordRepository equipmentOperationRecordRepository;


    public EquipmentOperationRecordServiceImpl(EquipmentOperationRecordRepository equipmentOperationRecordRepository) {
        this.equipmentOperationRecordRepository = equipmentOperationRecordRepository;
    }

    /**
     * Save a equipmentOperationRecord.
     *
     * @param equipmentOperationRecordDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EquipmentOperationRecordDTO save(EquipmentOperationRecordDTO equipmentOperationRecordDTO) {
        log.debug("Request to save EquipmentOperationRecord : {}", equipmentOperationRecordDTO);
        EquipmentOperationRecord equipmentOperationRecord = EquipmentOperationRecordService.toEntity(equipmentOperationRecordDTO);
        equipmentOperationRecord = equipmentOperationRecordRepository.save(equipmentOperationRecord);
        return EquipmentOperationRecordService.toDto(equipmentOperationRecord);
    }

    /**
     * Get all the equipmentOperationRecords.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentOperationRecordDTO> findAll(Pageable pageable, EquipmentOpRecordVM vm) {
        log.debug("Request to get all EquipmentOperationRecords");
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        QueryParams<EquipmentOperationRecord> queryParams = new QueryParams<>();

        queryParams.and("vehicle.merchant.id", Filter.Operator.eq, loginInfo.getMerchantId());


//        if (StringUtils.isNotEmpty(vm.getEquipmentId())) {
//            queryParams.and("equipment.identifyNumber", Filter.Operator.like, vm.getEquipmentId());
//        }

        if (StringUtils.isNotEmpty(vm.getDesc())) {
            queryParams.and("eventDesc", Filter.Operator.like, vm.getDesc());
        }

//        if (StringUtils.isNotEmpty(vm.getUserId())) {
//            queryParams.and("createdBy", Filter.Operator.like, vm.getUserId());
//        }

//        if (StringUtils.isNotEmpty(vm.getLicensePlateNumber())) {
//            queryParams.and("vehicle.licensePlateNumber", Filter.Operator.like, vm.getLicensePlateNumber());
//        }

        if (StringUtils.isNotEmpty(vm.getStartDate())) {
            Date startDate = DateUtils.parseDate(vm.getStartDate(), DateUtils.PATTERN_DATE);
            queryParams.and("createdDate", Filter.Operator.greaterThanOrEqualTo, startDate);
        }
        if (StringUtils.isNotEmpty(vm.getEndDate())) {
            Date endDate = DateUtils.parseDate(vm.getEndDate() + DateUtils.END_DATE_TIME, DateUtils.PATTERN_DATEALLTIME);
            queryParams.and("createdDate", Filter.Operator.lessThanOrEqualTo, endDate);
        }

        queryParams.addJoihFetch("vehicle", JoinType.LEFT);
        queryParams.addJoihFetch("equipment", JoinType.LEFT);
        Specification<EquipmentOperationRecord> specification = Specification.where(queryParams);
        if (StringUtils.isNotEmpty(vm.getUserId())) {
            Specification<EquipmentOperationRecord> itemSpecification = (Specification<EquipmentOperationRecord>) (root, criteriaQuery, criteriaBuilder) -> {
                Predicate identifyNumberPredicate = criteriaBuilder.like(root.get("equipment").get("identifyNumber"), "%" + vm.getUserId().trim() + "%");
                Predicate createdByPredicate = criteriaBuilder.like(root.get("createdBy"), "%" + vm.getUserId().trim() + "%");
                Predicate licensePlateNumberPredicate = criteriaBuilder.like(root.get("vehicle").get("licensePlateNumber"), "%" + vm.getUserId().trim() + "%");
                Predicate licensePlateNumberPredicateCn = criteriaBuilder.like(root.get("vehicle").get("licensePlateNumberCn"), "%" + vm.getUserId().trim() + "%");
                Predicate licensePlateNumberPredicateJp = criteriaBuilder.like(root.get("vehicle").get("licensePlateNumberJp"), "%" + vm.getUserId().trim() + "%");
                return criteriaBuilder.or(identifyNumberPredicate, createdByPredicate, licensePlateNumberPredicate, licensePlateNumberPredicateJp, licensePlateNumberPredicateCn);
            };
            specification = specification.and(itemSpecification);
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
            pageable.getSort().and(Sort.by(Sort.Direction.DESC, "createdDate")));

        return equipmentOperationRecordRepository.findAll(specification, pageable)
            .map(EquipmentOperationRecordService::toDto);
    }

    /**
     * Get one equipmentOperationRecord by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentOperationRecordDTO> findOne(Long id) {
        log.debug("Request to get EquipmentOperationRecord : {}", id);
        return equipmentOperationRecordRepository.findById(id)
            .map(EquipmentOperationRecordService::toDto);
    }

    /**
     * Delete the equipmentOperationRecord by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EquipmentOperationRecord : {}", id);
        equipmentOperationRecordRepository.deleteById(id);
    }
}
