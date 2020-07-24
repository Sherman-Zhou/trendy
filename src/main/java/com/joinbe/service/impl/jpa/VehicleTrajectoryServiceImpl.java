package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.DateUtils;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.config.Constants;
import com.joinbe.data.collector.service.DataCollectService;
import com.joinbe.data.collector.service.dto.VehicleCalcInfoResponseItemDTO;
import com.joinbe.domain.*;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.*;
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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
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


    private final VehicleRepository vehicleRepository;

    private final VehicleTrajectoryDetailsRepository trajectoryDetailsRepository;

    private final CityRepository cityRepository;

    private final ShopRepository shopRepository;

    private final StaffRepository staffRepository;

    private final DataCollectService dataCollectService;

    public VehicleTrajectoryServiceImpl(VehicleTrajectoryRepository vehicleTrajectoryRepository,
                                        VehicleRepository vehicleRepository, VehicleTrajectoryDetailsRepository trajectoryDetailsRepository,
                                        CityRepository cityRepository, ShopRepository shopRepository, StaffRepository staffRepository,
                                        DataCollectService dataCollectService) {
        this.vehicleTrajectoryRepository = vehicleTrajectoryRepository;
        this.vehicleRepository = vehicleRepository;
        this.trajectoryDetailsRepository = trajectoryDetailsRepository;
        this.cityRepository = cityRepository;
        this.shopRepository = shopRepository;
        this.staffRepository = staffRepository;
        this.dataCollectService = dataCollectService;
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
     * @param vm       the search conditions.
     * @param pageable
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VehicleTrajectoryDTO> findAll(Pageable pageable, TrajectoryVM vm) {
        log.debug("Request to get all VehicleTrajectories");
        QueryParams<VehicleTrajectory> queryParams = new QueryParams<>();
        if (vm.getVehicleId() != null) {
            queryParams.and("vehicle.id", Filter.Operator.eq, vm.getVehicleId());
        }
        if (StringUtils.isNotEmpty(vm.getTrajectoryId())) {
            queryParams.and("trajectoryId", Filter.Operator.like, vm.getTrajectoryId());
        }
        if (StringUtils.isNotEmpty(vm.getStartDate())) {
            Date startDate = DateUtils.parseDate(vm.getStartDate(), DateUtils.PATTERN_DATEALLTIME);
            queryParams.and("startTime", Filter.Operator.greaterThanOrEqualTo, startDate);
        }
        if (StringUtils.isNotEmpty(vm.getEndDate())) {
            Date endDate = DateUtils.parseDate(vm.getEndDate(), DateUtils.PATTERN_DATEALLTIME);
            queryParams.and("endTime", Filter.Operator.lessThanOrEqualTo, endDate);
        }

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
            pageable.getSort().and(Sort.by(Sort.Direction.DESC, "startTime")));
        return vehicleTrajectoryRepository.findAll(queryParams, pageable)
            .map(VehicleTrajectoryService::toDto);
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
            queryParams.and("vehicleTrajectory.trajectoryId", Filter.Operator.eq, vm.getTrajectoryId());
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

        return trajectoryDetailsRepository.findAll(queryParams, Sort.by(Sort.Direction.ASC, "receivedTime", "id"))
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
        //List<String> userDivisionIds = SecurityUtils.getCurrentUserDivisionIds();
        Staff staff = staffRepository.findOneWithShopsAndCitiesByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<DivisionWithVehicesleDTO> divisions = staff.getShops().stream().map(shop ->
            new DivisionWithVehicesleDTO(shop, LocaleContextHolder.getLocale())).collect(Collectors.toList());

        List<DivisionWithVehicesleDTO> divisionDTOInCities = staff.getCities().stream()
            .map(shop -> new DivisionWithVehicesleDTO(shop, LocaleContextHolder.getLocale())).collect(Collectors.toList());
        divisions.addAll(divisionDTOInCities);

//        List<DivisionWithVehicesleDTO> divisions = userDivisionIds.stream()
//            .map(id -> {
//                DivisionWithVehicesleDTO dto;
//                if (id.startsWith(Constants.CITY_ID_PREFIX)) {
//                    City city = cityRepository.getOne(id);
//                    dto = new DivisionWithVehicesleDTO(city, LocaleContextHolder.getLocale());
//                } else {
//                    Shop shop = shopRepository.getOne(id);
//                    dto = new DivisionWithVehicesleDTO(shop, LocaleContextHolder.getLocale());
//                }
//                return dto;
//            })
//
//            .filter(division -> RecordStatus.ACTIVE.equals(division.getStatus()))
//            .collect(Collectors.toList());

        QueryParams<Vehicle> queryParams = new QueryParams<>();
        queryParams.and("status", Filter.Operator.eq, RecordStatus.ACTIVE);
//        if (vm.getDivisionId() != null) {
//            SecurityUtils.checkDataPermission(vm.getDivisionId());
//            queryParams.and("division.id", Filter.Operator.eq, vm.getDivisionId());
//        } else {
//            // add user's division condition
//            queryParams.and("division.id", Filter.Operator.in, SecurityUtils.getCurrentUserDivisionIds());
//        } //FIXME: permission check
        if (vm.getOnlineOnly() != null) {
            if (vm.getOnlineOnly()) {
                queryParams.and("equipment.isOnline", Filter.Operator.eq, vm.getOnlineOnly());
            }
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

        Map<String, List<VehicleSummaryDTO>> vehicleMap = vehicleRepository.findAll(specification).stream()
            .map(vehicle -> {
                VehicleSummaryDTO dto = new VehicleSummaryDTO();
                dto.setLicensePlateNumber(vehicle.getLicensePlateNumber());
                dto.setIsMoving(vehicle.getIsMoving());
                dto.setId(vehicle.getId());
                dto.setDivisionId(vehicle.getShop().getId());
                return dto;
            })
            .collect(Collectors.groupingBy(VehicleSummaryDTO::getDivisionId));

        //group by parentId
        List<DivisionWithVehicesleDTO> children = divisions.stream()
            .peek(division -> division.setVehicles(vehicleMap.get(division.getId())))
            .filter(division -> division.getParentId() != null)
            .sorted(Comparator.comparing(DivisionDTO::getName))
            .collect(Collectors.toList());


        Map<String, List<DivisionDTO>> childMenusMap = children.stream().collect(Collectors.groupingBy(DivisionDTO::getParentId));
        boolean hasNotRootCity = divisions.stream().noneMatch(divisionDTO -> Constants.CITY_ROOT_ID.equals(divisionDTO.getId()));

        if (hasNotRootCity && childMenusMap.get(Constants.CITY_ROOT_ID) != null) {
            divisions.add(new DivisionWithVehicesleDTO(cityRepository.findById(Constants.CITY_ROOT_ID).get(), LocaleContextHolder.getLocale()));
        }

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
            SecurityUtils.checkDataPermission(vehicle.getShop());
            VehicleStateDTO vehicleStateDTO = new VehicleStateDTO();
            if (vehicle.getEquipment() != null) {
                EquipmentDTO equipment = EquipmentService.toDto(vehicle.getEquipment());

                vehicleStateDTO.setEquipment(equipment);
            }
            VehicleCalcInfoResponseItemDTO vehicleCalcInfoResponseItemDTO = dataCollectService.getVehicleMileageAndFuelCalc(vehicleId);
            if (vehicleCalcInfoResponseItemDTO != null) {
                vehicleStateDTO.setRemainingFuel(vehicleCalcInfoResponseItemDTO.getRemainFuelConsumptionInLiter());
                vehicleStateDTO.setTotalMileage(vehicleCalcInfoResponseItemDTO.getCurrentMileageInKM());
            }

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
        List<VehicleTrajectory> trajectories = vehicleTrajectoryRepository.findByVehicleIdOrderByTrajectoryIdDesc(vehicleId);
        return trajectories.stream().map(VehicleTrajectory::getTrajectoryId).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    @Override
    public List<TrajectoryReportDTO> findAllTrajectory4backup(Instant startTime, Instant endTime) {


        List<VehicleTrajectory> vehicleTrajectories = vehicleTrajectoryRepository.findTrajectoryAfter(startTime, endTime);

        List<TrajectoryReportDTO> reports = vehicleTrajectories.stream().map(VehicleTrajectory::getDetails)
            .flatMap(Collection::stream)
            .map(
                vehicleTrajectoryDetails -> {
                    TrajectoryReportDTO trajectoryReportDTO = new TrajectoryReportDTO();
                    trajectoryReportDTO.setReceivedTime(DateUtils.formatDate(vehicleTrajectoryDetails.getReceivedTime(), DateUtils.PATTERN_DATEALLTIME));
                    trajectoryReportDTO.setLat(vehicleTrajectoryDetails.getLatitude());
                    trajectoryReportDTO.setLng(vehicleTrajectoryDetails.getLongitude());
                    trajectoryReportDTO.setTrajectoryId(vehicleTrajectoryDetails.getVehicleTrajectory().getTrajectoryId());
                    Equipment equipment = vehicleTrajectoryDetails.getVehicleTrajectory().getEquipment();
                    if (equipment != null) {
                        trajectoryReportDTO.setIdentifyNumber(equipment.getIdentifyNumber());
                        trajectoryReportDTO.setImei(equipment.getImei());
                    }
                    return trajectoryReportDTO;
                }
            ).sorted(Comparator.comparing(TrajectoryReportDTO::getIdentifyNumber)
                .thenComparing(TrajectoryReportDTO::getTrajectoryId)
                .thenComparing(TrajectoryReportDTO::getReceivedTime))
            .collect(Collectors.toList());

        return reports;
    }
}
