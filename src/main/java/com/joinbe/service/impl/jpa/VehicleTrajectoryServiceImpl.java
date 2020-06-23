package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.DateUtils;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Vehicle;
import com.joinbe.domain.VehicleTrajectory;
import com.joinbe.domain.VehicleTrajectoryDetails;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.DivisionRepository;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.repository.VehicleTrajectoryDetailsRepository;
import com.joinbe.repository.VehicleTrajectoryRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.VehicleTrajectoryDetailsService;
import com.joinbe.service.VehicleTrajectoryService;
import com.joinbe.service.dto.*;
import com.joinbe.web.rest.vm.SearchVehicleVM;
import com.joinbe.web.rest.vm.TrajectoryVM;
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
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link VehicleTrajectory}.
 */
@Service("JpaVehicleTrajectoryService")
@Transactional
public class VehicleTrajectoryServiceImpl implements VehicleTrajectoryService {

    private final Logger log = LoggerFactory.getLogger(VehicleTrajectoryServiceImpl.class);

    private final VehicleTrajectoryRepository vehicleTrajectoryRepository;

    private final DivisionRepository divisionRepository;

    private final VehicleRepository vehicleRepository;

    private final VehicleTrajectoryDetailsRepository trajectoryDetailsRepository;

    public VehicleTrajectoryServiceImpl(VehicleTrajectoryRepository vehicleTrajectoryRepository, DivisionRepository divisionRepository,
                                        VehicleRepository vehicleRepository, VehicleTrajectoryDetailsRepository trajectoryDetailsRepository) {
        this.vehicleTrajectoryRepository = vehicleTrajectoryRepository;
        this.divisionRepository = divisionRepository;
        this.vehicleRepository = vehicleRepository;
        this.trajectoryDetailsRepository = trajectoryDetailsRepository;
    }

    /**
     * Save a vehicleTrajectory.
     *
     * @param vehicleTrajectoryDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VehicleTrajectoryDTO save(VehicleTrajectoryDTO vehicleTrajectoryDTO) {
        log.debug("Request to save VehicleTrajectory : {}", vehicleTrajectoryDTO);
        VehicleTrajectory vehicleTrajectory = VehicleTrajectoryService.toEntity(vehicleTrajectoryDTO);
        vehicleTrajectory = vehicleTrajectoryRepository.save(vehicleTrajectory);
        return VehicleTrajectoryService.toDto(vehicleTrajectory);
    }

    /**
     * Get all the vehicleTrajectories.
     *
     * @param vm the search conditions.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VehicleTrajectoryDTO> findAll(TrajectoryVM vm) {
        log.debug("Request to get all VehicleTrajectories");
        QueryParams<VehicleTrajectory> queryParams = new QueryParams<>();
        if (vm.getVehicleId() != null) {
            queryParams.and("vehicle.id", Filter.Operator.eq, vm.getVehicleId());
        }
        if (StringUtils.isNotEmpty(vm.getTrajectoryId())) {
            queryParams.and("trajectoryId", Filter.Operator.like, vm.getTrajectoryId());
        }
        if (StringUtils.isNotEmpty(vm.getStartDate())) {
            Date startDate = DateUtils.parseDate(vm.getStartDate(), DateUtils.PATTERN_DATE);
            queryParams.and("startTime", Filter.Operator.greaterThanOrEqualTo, startDate);
        }
        if (StringUtils.isNotEmpty(vm.getEndDate())) {
            Date endDate = DateUtils.parseDate(vm.getEndDate() + DateUtils.END_DATE_TIME, DateUtils.PATTERN_DATEALLTIME);
            queryParams.and("endTime", Filter.Operator.lessThanOrEqualTo, endDate);
        }
        return vehicleTrajectoryRepository.findAll(queryParams)
            .stream().map(VehicleTrajectoryService::toDto).collect(Collectors.toList());
    }


    /**
     * Get all the vehicleTrajectories.
     *
     * @param vm the search conditions.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VehicleTrajectoryDetailsDTO> findAllDetails(TrajectoryVM vm) {
        log.debug("Request to get all VehicleTrajectories");
        QueryParams<VehicleTrajectoryDetails> queryParams = new QueryParams<>();

        if (vm.getVehicleId() != null) {
            queryParams.and("vehicleTrajectory.vehicle.id", Filter.Operator.eq, vm.getVehicleId());
        }
        if (StringUtils.isNotEmpty(vm.getTrajectoryId())) {
            queryParams.and("vehicleTrajectory.trajectoryId", Filter.Operator.like, vm.getTrajectoryId());
        }
        if (vm.getTrajectoryIds() != null && !vm.getTrajectoryIds().isEmpty()) {
            queryParams.and("vehicleTrajectory.trajectoryId", Filter.Operator.in, vm.getTrajectoryIds());
        }
        if (StringUtils.isNotEmpty(vm.getStartDate())) {
            Date startDate = DateUtils.parseDate(vm.getStartDate(), DateUtils.PATTERN_DATE);
            queryParams.and("receivedTime", Filter.Operator.greaterThanOrEqualTo, startDate);
        }
        if (StringUtils.isNotEmpty(vm.getEndDate())) {
            Date endDate = DateUtils.parseDate(vm.getEndDate() + DateUtils.END_DATE_TIME, DateUtils.PATTERN_DATEALLTIME);
            queryParams.and("receivedTime", Filter.Operator.lessThanOrEqualTo, endDate);
        }
        return trajectoryDetailsRepository.findAll(queryParams)
            .stream().map(VehicleTrajectoryDetailsService::toDto).collect(Collectors.toList());
    }

    /**
     * Get one vehicleTrajectory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleTrajectoryDTO> findOne(Long id) {
        log.debug("Request to get VehicleTrajectory : {}", id);
        return vehicleTrajectoryRepository.findById(id)
            .map(VehicleTrajectoryService::toDto);
    }

    /**
     * Delete the vehicleTrajectory by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete VehicleTrajectory : {}", id);
        vehicleTrajectoryRepository.deleteById(id);
    }

    @Override
    public List<DivisionWithVehicesleDTO> findCurrentUserDivisionsAndVehicles(SearchVehicleVM vm) {
        List<Long> userDivisionIds = SecurityUtils.getCurrentUserDivisionIds();
        List<DivisionWithVehicesleDTO> divisions = userDivisionIds.stream()
            .map(divisionRepository::getOne)
            .map(division -> new DivisionWithVehicesleDTO(division))
            .filter(division -> RecordStatus.ACTIVE.equals(division.getStatus()))
            .collect(Collectors.toList());

        QueryParams<Vehicle> queryParams = new QueryParams<>();
        queryParams.and("status", Filter.Operator.eq, RecordStatus.ACTIVE);
        if (vm.getDivisionId() != null) {
            SecurityUtils.checkDataPermission(vm.getDivisionId());
            queryParams.and("division.id", Filter.Operator.eq, vm.getDivisionId());
        } else {
            // add user's division condition
            queryParams.and("division.id", Filter.Operator.in, SecurityUtils.getCurrentUserDivisionIds());
        }
        if (vm.getOnlineOnly() != null) {
            queryParams.and("equipment.isOnline", Filter.Operator.eq, vm.getOnlineOnly());
        }
        Specification<Vehicle> specification = Specification.where(queryParams);
        if (StringUtils.isNotEmpty(vm.getLicensePlateNumberOrDeviceId())) {
            //name or account search...
            Specification<Vehicle> itemSpecification = (Specification<Vehicle>) (root, criteriaQuery, criteriaBuilder) -> {
                Predicate namePredicate = criteriaBuilder.like(root.get("licensePlateNumber"), "%" + vm.getLicensePlateNumberOrDeviceId().trim() + "%");
                Predicate loginPredicate = criteriaBuilder.like(root.get("equipment").get("identifyNumber"), "%" + vm.getLicensePlateNumberOrDeviceId().trim() + "%");
                return criteriaBuilder.or(namePredicate, loginPredicate);
            };
            specification = specification.and(itemSpecification);
        }

        Map<Long, List<VehicleSummaryDTO>> vehicleMap = vehicleRepository.findAll(specification).stream()
            .map(vehicle -> {
                VehicleSummaryDTO dto = new VehicleSummaryDTO();
                dto.setLicensePlateNumber(vehicle.getLicensePlateNumber());
                dto.setIsMoving(vehicle.getIsMoving());
                dto.setId(vehicle.getId());
                dto.setDivisionId(vehicle.getDivision().getId());
                return dto;
            })
            .collect(Collectors.groupingBy(vehicle -> vehicle.getDivisionId()));

        //group by parentId
        List<DivisionWithVehicesleDTO> children = divisions.stream()
            .map(division -> {
                division.setVehicles(vehicleMap.get(division.getId()));
                return division;
            })
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
        List<DivisionWithVehicesleDTO> rootDivision = divisions.stream()
            .filter(menu -> menu.getParentId() == null)
            .collect(Collectors.toList());

        return rootDivision;
    }

    @Override
    public Optional<VehicleStateDTO> findVehicleCurrentState(Long vehicleId) {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);
        if (vehicleOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();
            SecurityUtils.checkDataPermission(vehicle.getDivision());
            VehicleStateDTO vehicleStateDTO = new VehicleStateDTO();
            if(vehicle.getEquipment()!=null) {
                EquipmentDTO equipment = EquipmentService.toDto(vehicle.getEquipment());

                vehicleStateDTO.setEquipment(equipment);
            }
            //TODO: to calculate....
            vehicleStateDTO.setRemainingFuel(BigDecimal.valueOf(60));
            vehicleStateDTO.setTotalMileage(vehicle.getTotalMileage());

            //to search the latest TrajectoryDetail...
            QueryParams<VehicleTrajectoryDetails> queryParams = new QueryParams<>();
            queryParams.and("vehicleTrajectory.vehicle.id", Filter.Operator.eq, vehicleId);

            Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("receivedTime")));

            Page<VehicleTrajectoryDetails> page = trajectoryDetailsRepository.findAll(queryParams, pageable);
            if (!page.getContent().isEmpty()) {
                VehicleTrajectoryDetailsDTO trajectoryDetailsDTO = VehicleTrajectoryDetailsService.toDto(page.getContent().get(0));
                vehicleStateDTO.setTrajectoryDetails(trajectoryDetailsDTO);
            }
            return Optional.of(vehicleStateDTO);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<String> findAllTrajectoryIds(Long vehicleId) {
        List<VehicleTrajectory> trajectories = vehicleTrajectoryRepository.findByVehicleId(vehicleId);
        return trajectories.stream().map(VehicleTrajectory::getTrajectoryId).collect(Collectors.toList());
    }
}
