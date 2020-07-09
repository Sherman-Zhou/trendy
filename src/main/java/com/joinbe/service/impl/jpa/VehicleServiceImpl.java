package com.joinbe.service.impl.jpa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joinbe.common.excel.BindingData;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.config.ApplicationProperties;
import com.joinbe.domain.Division;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.Vehicle;
import com.joinbe.domain.enumeration.EquipmentStatus;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.DivisionRepository;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.VehicleService;
import com.joinbe.service.dto.*;
import com.joinbe.service.util.RestfulClient;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.EquipmentVehicleBindingVM;
import com.joinbe.web.rest.vm.VehicleBindingVM;
import com.joinbe.web.rest.vm.VehicleVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Vehicle}.
 */
@Service("JpaVehicleService")
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final Logger log = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository vehicleRepository;

    private final EquipmentRepository equipmentRepository;

    private final DivisionRepository divisionRepository;

    private final MessageSource messageSource;

    private final RestfulClient restfulClient;

    private final ApplicationProperties applicationProperties;

    private ObjectMapper mapper;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, EquipmentRepository equipmentRepository,
                              DivisionRepository divisionRepository, MessageSource messageSource,
                              RestfulClient restfulClient, ApplicationProperties applicationProperties) {
        this.vehicleRepository = vehicleRepository;
        this.equipmentRepository = equipmentRepository;
        this.divisionRepository = divisionRepository;
        this.messageSource = messageSource;
        this.restfulClient = restfulClient;
        this.applicationProperties = applicationProperties;
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Save a vehicle.
     *
     * @param vehicleDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VehicleDetailsDTO save(VehicleDetailsDTO vehicleDetailsDTO) {
        log.debug("Request to save Vehicle : {}", vehicleDetailsDTO);
        Vehicle vehicle = VehicleService.toEntity(vehicleDetailsDTO);
        Long divisionId = vehicleDetailsDTO.getDivisionId();
        if (vehicleDetailsDTO.getId() != null) {
            divisionId = vehicleRepository.getOne(vehicleDetailsDTO.getId()).getDivision().getId();
        }
        Optional<Division> division = divisionRepository.findById(divisionId);
        if (division.isPresent()) {
            SecurityUtils.checkDataPermission(division.get());
            vehicle.setDivision(division.get());
        } else {
            throw new BadRequestAlertException("Division does not exist", "Vehicle", "divnotexists");
        }
        vehicle = vehicleRepository.save(vehicle);
        return VehicleService.toDto(vehicle);
    }

    /**
     * Get all the vehicles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VehicleDetailsDTO> findAll(Pageable pageable, VehicleVM vm) {
        log.debug("Request to get all Vehicles");
        QueryParams<Vehicle> queryParams = new QueryParams<>();
        if (vm.getDivisionId() != null) {
            SecurityUtils.checkDataPermission(vm.getDivisionId());
            queryParams.and("division.id", Filter.Operator.eq, vm.getDivisionId());
        } else {
            // add user's division condition
            List<Long> userDivisionIds = SecurityUtils.getCurrentUserDivisionIds();
            queryParams.and("division.id", Filter.Operator.in, userDivisionIds);
        }

        // only active vehicle...
        queryParams.and("status", Filter.Operator.eq, RecordStatus.ACTIVE);

        if (StringUtils.isNotEmpty(vm.getBrand())) {
            queryParams.and("brand", Filter.Operator.like, vm.getBrand());
        }
        if (StringUtils.isNotEmpty(vm.getName())) {
            queryParams.and("name", Filter.Operator.like, vm.getName());
        }

        if (StringUtils.isNotEmpty(vm.getLicensePlateNumber())) {
            queryParams.and("licensePlateNumber", Filter.Operator.like, vm.getLicensePlateNumber());
        }
        if (vm.getIsBounded() != null) {
            queryParams.and("bounded", Filter.Operator.eq, vm.getIsBounded());
        }

        if (vm.getIsOnline()!= null){
            queryParams.and("equipment.isOnline", Filter.Operator.eq, vm.getIsOnline());
        }

        if (vm instanceof VehicleBindingVM) {
            VehicleBindingVM bindingVM = (VehicleBindingVM) vm;
            if (StringUtils.isNotEmpty(bindingVM.getIdentifyNumber())) {
                queryParams.and("equipment.identifyNumber", Filter.Operator.like, bindingVM.getIdentifyNumber());
            }
        }


        return vehicleRepository.findAll(queryParams, pageable)
            .map(VehicleService::toDto);
    }

    /**
     * Get one vehicle by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleDetailsDTO> findOne(Long id) {
        log.debug("Request to get Vehicle : {}", id);
        return vehicleRepository.findById(id)
            .map(VehicleService::toDto);
    }

    @Override
    public void binding(EquipmentVehicleBindingVM vm) {
        log.debug("Request to binding equipment and vehicle: {}", vm);
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vm.getVehicleId()).filter(vehicle -> RecordStatus.ACTIVE.equals(vehicle.getStatus()));

        Optional<Equipment> equipmentOptional = equipmentRepository.findById(vm.getEquipmentId()).filter(equipment -> EquipmentStatus.DELETED.equals(equipment.getStatus()));


        if (!vehicleOptional.isPresent()) {
            throw new BadRequestAlertException("Invalid vehicle id", "Vehicle", "vehicle.notexist");
        }
        if (!equipmentOptional.isPresent()) {
            throw new BadRequestAlertException("Invalid equipment id", "Equipment", "equipment.notexist");
        }
        Vehicle vehicle = vehicleOptional.get();
        Equipment equipment = equipmentOptional.get();
        if (!EquipmentStatus.UNBOUND.equals(equipment.getStatus())) {
            throw new BadRequestAlertException("Equipment is bound already", "Binding", "equipment.binding.boundalready");
        }
        if (vehicle.getBounded()) {
            throw new BadRequestAlertException("Vehicle is bound already", "Binding", "vehicle.binding.boundalready");
        }
        equipment.setVehicle(vehicle);
        equipment.setStatus(EquipmentStatus.BOUND);
        vehicle.setBounded(true);
    }

    /**
     * Delete the vehicle by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Vehicle : {}", id);
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(id);
        if (vehicleOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();
            SecurityUtils.checkDataPermission(vehicle.getDivision());
            vehicle.setStatus(RecordStatus.DELETED);
            vehicle.setBounded(false);
            if (vehicle.getEquipment() != null) {
                vehicle.getEquipment().setStatus(EquipmentStatus.UNBOUND);
                vehicle.getEquipment().setVehicle(null);
            }
        }
        // vehicleRepository.deleteById(id);
    }

    @Override
    public List<VehicleSummaryDTO> findVehicleByDivisionId(Long divisionId) {
        List<VehicleSummaryDTO> vehicles = vehicleRepository.findByDivisionIdAndStatus(divisionId, RecordStatus.ACTIVE)
            .stream().map(VehicleService::toSummaryDto).collect(Collectors.toList());
        return vehicles;
    }

    @Override
    public void binding(UploadResponse response, List<BindingData> bindingDataList) {


        for (BindingData bindingData : bindingDataList) {
            boolean hasError = false;
            Optional<Vehicle> vehicleOptional = vehicleRepository.findOneByLicensePlateNumberAndStatus(bindingData.getLicensePlateNumber(), RecordStatus.ACTIVE);
            Optional<Equipment> equipmentOptional = equipmentRepository.findOneByIdentifyNumberAndStatusNot(bindingData.getIdentifyNumber(), EquipmentStatus.DELETED);

            if (!vehicleOptional.isPresent()) {
                createResult("binding.upload.vehicle.not.exist", bindingData.getRowIdx(), false, response.getErrors());
                hasError = true;
            }
            if (!equipmentOptional.isPresent()) {
                createResult("binding.upload.equipment.not.exist", bindingData.getRowIdx(), false, response.getErrors());
                hasError = true;

            }
            if (vehicleOptional.isPresent() && equipmentOptional.isPresent()) {
                Vehicle vehicle = vehicleOptional.get();
                Equipment equipment = equipmentOptional.get();
                if (!EquipmentStatus.UNBOUND.equals(equipment.getStatus())) {
                    createResult("binding.upload.equipment.bound.already", bindingData.getRowIdx(), false, response.getErrors());
                    hasError = true;
                }
                if (vehicle.getBounded()) {
                    createResult("binding.upload.vehicle.bound.already", bindingData.getRowIdx(), false, response.getErrors());
                    hasError = true;
                }
            }
            if (!hasError) {
                Vehicle vehicle = vehicleOptional.get();
                Equipment equipment = equipmentOptional.get();
                equipment.setVehicle(vehicle);
                equipment.setStatus(EquipmentStatus.BOUND);
                vehicle.setBounded(true);
            } else {
                response.successToError();
            }
        }

    }

    @Override
    public void syncVehicle() {
            int fetchSize  = 9999 ;
            Map<String, Division> divisionMap = new HashMap<>();
            Division root = new Division();
            root.setId(1L);
            Map<String, String> urlParams = new HashMap<>();
            urlParams.put("pagesize", String.valueOf(fetchSize));
            ApplicationProperties.TrendyConfig trendyConfig = applicationProperties.getTrendy();
            TrendyResponse response = restfulClient.getForObject(trendyConfig.getUrl() + trendyConfig.getVehiclePath(), TrendyResponse.class,urlParams);
            if(response.getStatus() != HttpStatus.OK.value()) {
                throw new BadRequestAlertException(response.getInfo(), "vehicle.sync", "vehicle.sync.error");
            }
           List<TrendyResponse.Car> cars =   response.getData().getList();
            for(TrendyResponse.Car car : cars) {
                Division shop = null;
                Division city = null;
                if(car.getCitylist()!=null) {
                    Division cityInDb = divisionMap.get(car.getCitylist().getName());
                    if(cityInDb == null) {
                        Optional<Division> division = divisionRepository.findByNameAndStatus(car.getCitylist().getName(), RecordStatus.ACTIVE);
                        if(division.isPresent()) {
                            cityInDb = division.get();
                        }
                    }

                    if(cityInDb != null){
                        city = cityInDb;
                        divisionMap.put(car.getCitylist().getName(), cityInDb);
                        city.setTrendyId(car.getCitylist().getId());
                    }else {
                        city = new Division();
                        city.setName(car.getCitylist().getName());
                        city.setDescription(city.getName());
                        city.setStatus(RecordStatus.ACTIVE);
                        city.setParent(root);
                        city.setTrendyId(car.getCitylist().getId());
                        divisionRepository.save(city);
                        divisionMap.put(car.getCitylist().getName(), city);
                    }
                }
                if(car.getShoplist()!= null) {
                    Division shopInDb = divisionMap.get(car.getShoplist().getTitle());
                    if(shopInDb == null) {
                        Optional<Division> division = divisionRepository.findByNameAndStatus(car.getShoplist().getTitle(), RecordStatus.ACTIVE);
                        if(division.isPresent()) {
                            shopInDb = division.get();
                        }
                    }

                    if(shopInDb !=null){
                        shop = shopInDb;
                        shop.setTrendyId(car.getShoplist().getId());
                        divisionMap.put(car.getShoplist().getTitle(), shop);
                        log.debug("the shop's city in db is {}, the city in trendy: {}", shop.getParent(), car.getCitylist());
                    }else {
                        shop = new Division();
                        shop.setName(car.getShoplist().getTitle());
                        shop.setDescription(shop.getName());
                        shop.setStatus(RecordStatus.ACTIVE);
                        shop.setTrendyId(car.getShoplist().getId());
                        if(city!=null) {
                            shop.setParent(city);
                        }
                        divisionRepository.save(shop);
                        divisionMap.put(car.getShoplist().getTitle(), shop);
                    }
                }

                Optional<Vehicle> vehicleOptional = vehicleRepository.findOneByLicensePlateNumberAndStatus(car.getPlate_number(), RecordStatus.ACTIVE);
                Vehicle vehicle;
                if(vehicleOptional.isPresent()){
                      vehicle = vehicleOptional.get();
                }else {
                    vehicle = new Vehicle();
                }
                vehicle.setLicensePlateNumber(car.getPlate_number());
                vehicle.setBrand(car.getBrand_name());
                vehicle.setStyle(car.getBrand_style());
                vehicle.setType(car.getBrand_model());
                vehicle.setProdYear(car.getYear());
                vehicle.setName(car.getTitle());
                vehicle.setContactName(car.getUser_name());
                vehicle.setDivision(divisionMap.get(car.getShoplist().getTitle()));
                vehicle.setTrendyId(car.getId());
                vehicle.setStatus(RecordStatus.ACTIVE);
                vehicle.setBounded(false);
                vehicle.setIsMoving(false);
                vehicleRepository.save(vehicle);
            }
    }

    private void createResult(String msgKey, int rowIdx, boolean success, List<RowParseError> results) {
        String message = messageSource.getMessage(msgKey, new String[]{String.valueOf(rowIdx)}, LocaleContextHolder.getLocale());
        RowParseError result = new RowParseError();
        result.setMsg(message);
        result.setRowNum((long) rowIdx);
        results.add(result);
    }

}
