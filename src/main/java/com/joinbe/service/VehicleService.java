package com.joinbe.service;

import com.joinbe.common.excel.BindingData;
import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.City;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.Shop;
import com.joinbe.domain.Vehicle;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.dto.UploadResponse;
import com.joinbe.service.dto.VehicleDetailsDTO;
import com.joinbe.service.dto.VehicleSummaryDTO;
import com.joinbe.web.rest.vm.EquipmentVehicleBindingVM;
import com.joinbe.web.rest.vm.VehicleVM;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.Vehicle}.
 */
public interface VehicleService {

    static VehicleDetailsDTO toDto(Vehicle vehicle) {
        Locale locale = LocaleContextHolder.getLocale();

        VehicleDetailsDTO dto = BeanConverter.toDto(vehicle, VehicleDetailsDTO.class);
        dto.setStatus(vehicle.getStatus() != null ? vehicle.getStatus().getCode() : null);
        Shop shop = vehicle.getShop();
        City city = vehicle.getCity();
        SecurityUtils.checkDataPermission(shop);

        dto.setShopId(shop.getId());
        if (Locale.CHINESE.equals(locale)) {
            //city & shop
            dto.setOrgName(shop.getTitleCn());
            dto.setDivName(city.getNameCn());
            //vehicle i18n
            dto.setBrand(vehicle.getBrandCn());
            dto.setContactName(vehicle.getContactNameCn());
            dto.setName(vehicle.getNameCn());
            dto.setStyle(vehicle.getStyleCn());
            dto.setType(vehicle.getTypeCn());
            dto.setLicensePlateNumber(vehicle.getLicensePlateNumberCn());

        } else if (Locale.JAPANESE.equals(locale)) {
            dto.setOrgName(shop.getTitleJp());
            dto.setDivName(city.getNameJp());

            //vehicle i18n
            dto.setBrand(vehicle.getBrandJp());
            dto.setContactName(vehicle.getContactNameJp());
            dto.setName(vehicle.getNameJp());
            dto.setStyle(vehicle.getStyleJp());
            dto.setType(vehicle.getTypeJp());
            dto.setLicensePlateNumber(vehicle.getLicensePlateNumberJp());
        } else {
            dto.setOrgName(shop.getTitle());
            dto.setDivName(city.getName());
        }
        Equipment equipment = vehicle.getEquipment();
        if (equipment != null) {
            dto.setIsOnline(vehicle.getEquipment().getOnline());
            dto.setIdentifyNumber(equipment.getIdentifyNumber());
        }
        return dto;
    }

    static VehicleSummaryDTO toSummaryDto(Vehicle vehicle) {
        VehicleSummaryDTO summaryDTO = new VehicleSummaryDTO();
        summaryDTO.setId(vehicle.getId());
        summaryDTO.setLicensePlateNumber(vehicle.getLicensePlateNumber());
        summaryDTO.setIsMoving(vehicle.getIsMoving());
        return summaryDTO;
    }

    static Vehicle toEntity(VehicleDetailsDTO vehicleDetailsDTO) {

        return BeanConverter.toEntity(vehicleDetailsDTO, Vehicle.class);
    }

    /**
     * Save a vehicle.
     *
     * @param vehicleDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    VehicleDetailsDTO save(VehicleDetailsDTO vehicleDetailsDTO);

    /**
     * Get all the vehicles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VehicleDetailsDTO> findAll(Pageable pageable, VehicleVM vm);

    /**
     * Get the "id" vehicle.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VehicleDetailsDTO> findOne(Long id);


    void binding(EquipmentVehicleBindingVM vm);

    Optional<VehicleDetailsDTO> unbound(Long vehicleId);

    /**
     * Delete the "id" vehicle.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<VehicleSummaryDTO> findVehicleByDivisionId(String divisionId);

    void binding(UploadResponse response, List<BindingData> data);

    void syncVehicle();

}
