package com.joinbe.service;

import com.joinbe.common.excel.BindingData;
import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.Division;
import com.joinbe.domain.Vehicle;
import com.joinbe.security.SecurityUtils;
import com.joinbe.service.dto.UploadResponse;
import com.joinbe.service.dto.VehicleDetailsDTO;
import com.joinbe.service.dto.VehicleSummaryDTO;
import com.joinbe.web.rest.vm.EquipmentVehicleBindingVM;
import com.joinbe.web.rest.vm.VehicleVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.joinbe.domain.Vehicle}.
 */
public interface VehicleService {

    static VehicleDetailsDTO toDto(Vehicle vehicle) {
        VehicleDetailsDTO dto = BeanConverter.toDto(vehicle, VehicleDetailsDTO.class);
        dto.setStatus(vehicle.getStatus() != null ? vehicle.getStatus().getCode() : null);
        Division division = vehicle.getDivision();
        SecurityUtils.checkDataPermission(division);
        dto.setDivisionId(division.getId());
        dto.setOrgName(division.getName());
        if (division.getParent() != null) {
            dto.setDivName(division.getParent().getName());
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

    /**
     * Delete the "id" vehicle.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<VehicleSummaryDTO> findVehicleByDivisionId(Long divisionId);

    void binding(UploadResponse response, List<BindingData> data);

}
