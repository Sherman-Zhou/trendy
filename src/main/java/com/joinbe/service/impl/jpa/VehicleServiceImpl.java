package com.joinbe.service.impl.jpa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joinbe.common.excel.BindingData;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.config.ApplicationProperties;
import com.joinbe.config.Constants;
import com.joinbe.domain.*;
import com.joinbe.domain.enumeration.EquipmentStatus;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.CityRepository;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.repository.ShopRepository;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.security.UserLoginInfo;
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

import javax.persistence.criteria.JoinType;
import java.util.*;
import java.util.function.Function;
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

    private final ShopRepository shopRepository;

    private final CityRepository cityRepository;

    private final MessageSource messageSource;

    private final RestfulClient restfulClient;

    private final ApplicationProperties applicationProperties;

    private final ObjectMapper mapper;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, EquipmentRepository equipmentRepository,
                              ShopRepository shopRepository, CityRepository cityRepository, MessageSource messageSource,
                              RestfulClient restfulClient, ApplicationProperties applicationProperties) {
        this.vehicleRepository = vehicleRepository;
        this.equipmentRepository = equipmentRepository;
        this.shopRepository = shopRepository;
        this.cityRepository = cityRepository;
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
        String shopId = vehicleDetailsDTO.getShopId();
        String cityId = vehicleDetailsDTO.getCityId();
        if (vehicleDetailsDTO.getId() != null) {
            Vehicle vehicleInDb = vehicleRepository.getOne(vehicleDetailsDTO.getId());
            shopId = vehicleInDb.getShop().getId();
            cityId = vehicleInDb.getCity().getId();
        } else {
//            Optional<Division> division = divisionRepository.findById(divisionId);
//            if (division.isPresent()) {
//                SecurityUtils.checkDataPermission(division.get());
//                vehicle.setShop(division.get());
//            } else {
//                throw new BadRequestAlertException("Division does not exist", "Vehicle", "divnotexists");
//            }
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
        if (StringUtils.isEmpty(vm.getDivisionId()) || Constants.CITY_ROOT_ID.equals(vm.getDivisionId())) {
            // add user's shop condition
            UserLoginInfo userLoginInfo = SecurityUtils.getCurrentUserLoginInfo();
            List<String> shopIds = userLoginInfo.getDivisionIds().stream().filter(id -> id.startsWith(Constants.SHOP_ID_PREFIX))
                .collect(Collectors.toList());
            queryParams.and("shop.id", Filter.Operator.in, shopIds);
        } else {
            SecurityUtils.checkDataPermission(vm.getDivisionId());
            if (vm.getDivisionId().startsWith(Constants.CITY_ID_PREFIX)) {
                List<String> shopIds = shopRepository.findByCityId(vm.getDivisionId())
                    .stream().map(Shop::getId).collect(Collectors.toList());
                queryParams.and("shop.id", Filter.Operator.in, shopIds);
            } else {
                queryParams.and("shop.id", Filter.Operator.eq, vm.getDivisionId());
            }
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

        if (vm.getIsOnline() != null) {
            queryParams.and("equipment.isOnline", Filter.Operator.eq, vm.getIsOnline());
        }

        if (vm instanceof VehicleBindingVM) {
            VehicleBindingVM bindingVM = (VehicleBindingVM) vm;
            if (StringUtils.isNotEmpty(bindingVM.getIdentifyNumber())) {
                queryParams.and("equipment.identifyNumber", Filter.Operator.like, bindingVM.getIdentifyNumber());
            }
        }
        queryParams.addJoihFetch("equipment", JoinType.LEFT);

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
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        log.debug("Request to binding equipment and vehicle: {}", vm);
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vm.getVehicleId()).filter(vehicle -> RecordStatus.ACTIVE.equals(vehicle.getStatus()));

        Optional<Equipment> equipmentOptional = equipmentRepository.findById(vm.getEquipmentId()).filter(equipment -> !EquipmentStatus.DELETED.equals(equipment.getStatus()));


        if (!vehicleOptional.isPresent()) {
            throw new BadRequestAlertException("Invalid vehicle id", "Vehicle", "vehicle.notexist");
        }
        if (!equipmentOptional.isPresent()) {
            throw new BadRequestAlertException("Invalid equipment id", "Equipment", "equipment.notexist");
        }
        Vehicle vehicle = vehicleOptional.get();

        Equipment equipment = equipmentOptional.get();

        SecurityUtils.checkMerchantPermission(vehicle.getMerchant());
        SecurityUtils.checkMerchantPermission(equipment.getMerchant());

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

    @Override
    public Optional<VehicleDetailsDTO> unbound(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
            .map(vehicle -> {
                SecurityUtils.checkMerchantPermission(vehicle.getMerchant().getId());
                vehicle.setBounded(false);
                SecurityUtils.checkMerchantPermission(vehicle.getMerchant());

                Equipment equipment = vehicle.getEquipment();
                if (equipment != null) {
                    SecurityUtils.checkMerchantPermission(equipment.getMerchant());
                    equipment.setVehicle(null);
                    equipment.setStatus(EquipmentStatus.UNBOUND);
                } else {
                    log.warn("no equipment is found for vehiche: {}", vehicleId);
                }
                return VehicleService.toDto(vehicle);
            });
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
            SecurityUtils.checkDataPermission(vehicle.getShop());
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
    public List<VehicleSummaryDTO> findVehicleByDivisionId(String divisionId) {
        return vehicleRepository.findByShopIdAndStatus(divisionId, RecordStatus.ACTIVE)
            .stream().map(VehicleService::toSummaryDto).collect(Collectors.toList());
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
        UserLoginInfo userLoginInfo = SecurityUtils.getCurrentUserLoginInfo();
        Merchant userMerchant = new Merchant();
        userMerchant.setId(userLoginInfo.getMerchantId());

        int fetchSize = 9999;
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("pagesize", String.valueOf(fetchSize));
        ApplicationProperties.TrendyConfig trendyConfig = applicationProperties.getTrendy();
        TrendyResponse response = restfulClient.getForObject(trendyConfig.getUrl() + trendyConfig.getVehiclePath(), TrendyResponse.class, urlParams);
        if (response.getStatus() != HttpStatus.OK.value()) {
            throw new BadRequestAlertException(response.getInfo(), "vehicle.sync", "vehicle.sync.error");
        }
        //already in DB...
        Map<String, Shop> shopsInDb = shopRepository.findByMerchantId(userLoginInfo.getMerchantId()).stream().collect(Collectors.toMap(Shop::getId, Function.identity()));
        Map<String, City> citiesInDb = cityRepository.findByMerchantId(userLoginInfo.getMerchantId()).stream().collect(Collectors.toMap(City::getId, Function.identity()));

        Map<String, TrendyResponse.City> allCities = new HashMap<>();
        Map<String, TrendyResponse.Shop> allShops = new HashMap<>();

        Map<String, TrendyResponse.City> allCitiesJp = new HashMap<>();
        Map<String, TrendyResponse.Shop> allShopsJp = new HashMap<>();

        Map<String, TrendyResponse.City> allCitiesCn = new HashMap<>();
        Map<String, TrendyResponse.Shop> allShopsCn = new HashMap<>();

        List<Shop> newShops = new ArrayList<>();
        List<City> newCities = new ArrayList<>();
        List<Vehicle> newVehicles = new ArrayList<>();

        List<TrendyResponse.Car> jp = response.getData().getRow().getJp();
        List<TrendyResponse.Car> cn = response.getData().getRow().getCn();
        List<TrendyResponse.Car> en = response.getData().getRow().getEn();

        Map<String, TrendyResponse.Car> jpCarMap = jp.stream().collect(Collectors.toMap(TrendyResponse.Car::getId, Function.identity()));
        Map<String, TrendyResponse.Car> cnCarMap = cn.stream().collect(Collectors.toMap(TrendyResponse.Car::getId, Function.identity()));


        getShopsAndCities(allCities, allShops, en);
        getShopsAndCities(allCitiesJp, allShopsJp, jp);
        getShopsAndCities(allCitiesCn, allShopsCn, cn);
        //start to save City:
        Collection<TrendyResponse.City> cities = allCities.values();

        for (TrendyResponse.City trendyCity : cities) {
            City city;
            City cityInDb = citiesInDb.get(Constants.CITY_ID_PREFIX + trendyCity.getId());
            log.debug("start to process city: {}", trendyCity.getId());
            if (cityInDb != null) {
                log.debug("existing city");
                city = cityInDb;
            } else {
                log.debug("new City");
                city = new City();
                city.setMerchant(userMerchant);
                city.setId(Constants.CITY_ID_PREFIX + trendyCity.getId());
                citiesInDb.put(city.getId(), city);
                newCities.add(city);
            }
            city.setLat(trendyCity.getLat());
            city.setLng(trendyCity.getLng());
            city.setName(trendyCity.getName());

            if (allCitiesJp.get(trendyCity.getId()) != null) {
                TrendyResponse.City cityJp = allCitiesJp.get(trendyCity.getId());
                city.setNameJp(cityJp.getName());
            }

            if (allCitiesCn.get(trendyCity.getId()) != null) {
                TrendyResponse.City cityCn = allCitiesCn.get(trendyCity.getId());
                city.setNameCn(cityCn.getName());
            }
            city.setStatus(RecordStatus.ACTIVE);
            city.setParentId(trendyCity.getParentId());
        }
        City root = citiesInDb.get(Constants.CITY_ROOT_ID);
        Collection<City> allCitiesInDb = citiesInDb.values();
        for (City city : allCitiesInDb) {
            if (Constants.CITY_ROOT_ID.equals(city.getId())) {
                continue;
            }
            if (StringUtils.isNotEmpty(city.getParentId())) {
                City parent = citiesInDb.get(Constants.CITY_ID_PREFIX + city.getParentId());
                parent.getChildren().add(city);
                city.setParent(parent);
            } else {
                city.setParent(root);
                root.getChildren().add(city);
            }
        }
        cityRepository.save(root);

        //start to save Shop...
        Collection<TrendyResponse.Shop> shops = allShops.values();
        for (TrendyResponse.Shop trendyShop : shops) {
            Shop shop;
            Shop shopInDb = shopsInDb.get(Constants.SHOP_ID_PREFIX + trendyShop.getId());
            log.debug("start to process shop {}", trendyShop.getId());
            if (shopInDb != null) {
                log.debug("existing shop");
                shop = shopInDb;
            } else {
                log.debug("new shop");
                shop = new Shop();
                shop.setMerchant(userMerchant);
                shop.setId(Constants.SHOP_ID_PREFIX + trendyShop.getId());
                shopsInDb.put(shop.getId(), shop);
                newShops.add(shop);
            }
            shop.setTitle(trendyShop.getTitle());
            shop.setAddress(trendyShop.getAddress());
            if (allShopsJp.get(trendyShop.getId()) != null) {
                TrendyResponse.Shop shopJp = allShopsJp.get(trendyShop.getId());
                shop.setTitleJp(shopJp.getTitle());
                shop.setAddressJp(shopJp.getAddress());
            }

            if (allShopsCn.get(trendyShop.getId()) != null) {
                TrendyResponse.Shop shopCn = allShopsCn.get(trendyShop.getId());
                shop.setTitleCn(shopCn.getTitle());
                shop.setAddressCn(shopCn.getAddress());
            }
            shop.setCity(citiesInDb.get(Constants.CITY_ID_PREFIX + trendyShop.getCity()));
            // shop.setArea(citiesInDb.get(Constants.CITY_ID_PREFIX + trendyShop.getArea()));
            shop.setStatus(RecordStatus.ACTIVE);
            shopRepository.save(shop);
        }

        for (TrendyResponse.Car car : en) {

            Long carId = Long.parseLong(car.getId());
            log.debug("start to process car: {}", carId);
            Optional<Vehicle> vehicleOptional = vehicleRepository.findById(carId);
            Vehicle vehicle;
            if (vehicleOptional.isPresent()) {
                log.debug("existing car");
                vehicle = vehicleOptional.get();
            } else {
                log.debug("new car...");
                vehicle = new Vehicle();
                vehicle.setMerchant(userMerchant);
                vehicle.setId(carId);
                vehicle.setBounded(false);
                vehicle.setIsMoving(false);
                newVehicles.add(vehicle);
            }
            vehicle.setLicensePlateNumber(car.getPlate_number());
            vehicle.setBrand(car.getBrand_name());
            vehicle.setStyle(car.getBrand_style());
            vehicle.setType(car.getBrand_model());
            vehicle.setProdYear(car.getYear());
            vehicle.setName(car.getTitle());
            vehicle.setContactName(car.getUser_name());

            if (jpCarMap.get(car.getId()) != null) {
                TrendyResponse.Car jpCar = jpCarMap.get(car.getId());
                vehicle.setLicensePlateNumber(jpCar.getPlate_number());
                vehicle.setBrandJp(jpCar.getBrand_name());
                vehicle.setStyleJp(jpCar.getBrand_style());
                vehicle.setTypeJp(jpCar.getBrand_model());
                vehicle.setNameJp(jpCar.getTitle());
                vehicle.setContactNameJp(jpCar.getUser_name());
            }

            if (cnCarMap.get(car.getId()) != null) {
                TrendyResponse.Car jpCar = cnCarMap.get(car.getId());
                vehicle.setLicensePlateNumberCn(jpCar.getPlate_number());
                vehicle.setBrandCn(jpCar.getBrand_name());
                vehicle.setStyleCn(jpCar.getBrand_style());
                vehicle.setTypeCn(jpCar.getBrand_model());
                vehicle.setNameCn(jpCar.getTitle());
                vehicle.setContactNameCn(jpCar.getUser_name());
            }

            vehicle.setStatus(RecordStatus.ACTIVE);
            TrendyResponse.Shop shop = car.getShoplist();
            if (shop != null) {
                vehicle.setShop(shopsInDb.get(Constants.SHOP_ID_PREFIX + shop.getId()));
            }
            if (car.getCitylist() != null) {
                vehicle.setCity(citiesInDb.get(Constants.CITY_ID_PREFIX + car.getCitylist().getId()));
            }
            vehicleRepository.save(vehicle);
        }

    }

    private void getShopsAndCities(Map<String, TrendyResponse.City> allCities, Map<String, TrendyResponse.Shop> allShops, List<TrendyResponse.Car> en) {
        for (TrendyResponse.Car car : en) {
            if (car.getCitylist() != null) {
                allCities.putIfAbsent(car.getCitylist().getId(), car.getCitylist());
                //exactChildren(car.getCitylist(), allCities);
            }
            if (car.getShoplist() != null) {
                allShops.putIfAbsent(car.getShoplist().getId(), car.getShoplist());
            }
        }
    }

    private void exactChildren(TrendyResponse.City city, Map<String, TrendyResponse.City> allCities) {
        if (city.getSon() != null) {
            for (TrendyResponse.City son : city.getSon()) {
                son.setParentId(city.getId());
                allCities.putIfAbsent(son.getId(), son);
                if (son.getSon() != null) {
                    //exact areas if any...
                    exactChildren(son, allCities);
                }
            }
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
