package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.City;
import com.joinbe.domain.EquipmentFault;
import com.joinbe.domain.Shop;
import com.joinbe.domain.Vehicle;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.dto.EquipmentFaultDTO;
import com.joinbe.service.dto.EquipmentFaultHandleDTO;
import com.joinbe.web.rest.vm.EquipmentFaultVM;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.EquipmentFault}.
 */
public interface EquipmentFaultService {

    static EquipmentFaultDTO toDto(EquipmentFault equipmentFault) {

        EquipmentFaultDTO dto = BeanConverter.toDto(equipmentFault, EquipmentFaultDTO.class);
        if(equipmentFault.getEquipment()!=null){
            dto.setIdentifyNumber(equipmentFault.getEquipment().getIdentifyNumber());
            dto.setEquipmentId(equipmentFault.getEquipment().getId());
        }
        Vehicle vehicle = equipmentFault.getVehicle();
        if(vehicle != null) {
            //dto.setLicensePlateNumber(vehicle.getLicensePlateNumber());
            dto.setVehicleId(vehicle.getId());
            Shop shop = vehicle.getShop();
            City city = vehicle.getCity();
            SecurityUtils.checkDataPermission(shop);
            Locale locale = LocaleContextHolder.getLocale();
            if (Locale.CHINESE.equals(locale)) {
                //city & shop
                dto.setOrgName(shop.getTitleCn());
                dto.setDivName(city.getNameCn());
            } else if (Locale.JAPANESE.equals(locale)) {
                dto.setOrgName(shop.getTitleJp());
                dto.setDivName(city.getNameJp());
            } else {
                dto.setOrgName(shop.getTitle());
                dto.setDivName(city.getName());
            }
        }
        return  dto;
    }

    static EquipmentFault toEntity(EquipmentFaultDTO equipmentFaultDTO) {

        return BeanConverter.toEntity(equipmentFaultDTO, EquipmentFault.class);
    }

    /**
     * Save a equipmentFault.
     *
     * @param equipmentFaultDTO the entity to save.
     * @return the persisted entity.
     */
    EquipmentFaultDTO save(EquipmentFaultDTO equipmentFaultDTO);

    Optional<EquipmentFaultDTO> read(Long id);

    void batchRead(List<Long> ids);

    /**
     * Get all the equipmentFaults.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EquipmentFaultDTO> findAll(Pageable pageable, EquipmentFaultVM vm);

    /**
     * Get the "id" equipmentFault.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EquipmentFaultDTO> findOne(Long id);

    /**
     * Delete the "id" equipmentFault.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Optional<EquipmentFaultDTO> handle(EquipmentFaultHandleDTO equipmentFaultHandleDTO);
}
