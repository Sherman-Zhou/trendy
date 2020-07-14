package com.joinbe.service.impl.jpa;

import com.joinbe.common.excel.EquipmentData;
import com.joinbe.common.util.BeanConverter;
import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Equipment;
import com.joinbe.domain.Merchant;
import com.joinbe.domain.Vehicle;
import com.joinbe.domain.enumeration.EquipmentStatus;
import com.joinbe.repository.EquipmentFaultRepository;
import com.joinbe.repository.EquipmentOperationRecordRepository;
import com.joinbe.repository.EquipmentRepository;
import com.joinbe.repository.VehicleRepository;
import com.joinbe.security.SecurityUtils;
import com.joinbe.security.UserLoginInfo;
import com.joinbe.service.EquipmentService;
import com.joinbe.service.dto.EquipmentDTO;
import com.joinbe.service.dto.RowParseError;
import com.joinbe.service.dto.UploadResponse;
import com.joinbe.web.rest.errors.BadRequestAlertException;
import com.joinbe.web.rest.vm.EquipmentVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Equipment}.
 */
@Service("JpaEquipmentService")
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private final Logger log = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentRepository equipmentRepository;

    private final VehicleRepository vehicleRepository;

    private final EquipmentFaultRepository equipmentFaultRepository;

    private final EquipmentOperationRecordRepository operationRecordRepository;


    private final MessageSource messageSource;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, VehicleRepository vehicleRepository,
                                EquipmentFaultRepository equipmentFaultRepository,
                                EquipmentOperationRecordRepository operationRecordRepository, MessageSource messageSource) {
        this.equipmentRepository = equipmentRepository;
        this.vehicleRepository = vehicleRepository;
        this.equipmentFaultRepository = equipmentFaultRepository;
        this.operationRecordRepository = operationRecordRepository;

        this.messageSource = messageSource;
    }

    /**
     * Save a equipment.
     *
     * @param equipmentDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EquipmentDTO save(EquipmentDTO equipmentDTO) {
        log.debug("Request to save Equipment : {}", equipmentDTO);
        Equipment equipment;
        UserLoginInfo userLoginInfo = SecurityUtils.getCurrentUserLoginInfo();

        Optional<Equipment> equipmentByIdNum = equipmentRepository.findOneByIdentifyNumberAndStatusNot(equipmentDTO.getIdentifyNumber(), EquipmentStatus.DELETED);

        Optional<Equipment> equipmentByImei = equipmentRepository.findOneByImeiAndStatusNot(equipmentDTO.getImei(), EquipmentStatus.DELETED);


        if (equipmentDTO.getId() != null) {
            log.debug("updating Equipment");
            equipment = equipmentRepository.getOne(equipmentDTO.getId());
            if (equipmentByIdNum.isPresent() && !equipmentByIdNum.get().getId().equals(equipment.getId())) {
                throw new BadRequestAlertException("the IdentifyNumber is existed ", "Equipment", "equipment.upload.equipmentId.exists");
            }
            if (equipmentByImei.isPresent() && !equipmentByImei.get().getId().equals(equipment.getId())) {
                throw new BadRequestAlertException("the IMEI is existed ", "Equipment", "equipment.upload.imei.exists");
            }
            if (StringUtils.isNotEmpty(equipmentDTO.getBluetoothName())) {
                Optional<Equipment> equipmentByBluetoothName = equipmentRepository.findOneByBluetoothName(equipmentDTO.getBluetoothName());
                if (equipmentByBluetoothName.isPresent() && !equipmentByBluetoothName.get().getId().equals(equipment.getId())) {
                    throw new BadRequestAlertException("the Bluetooth Name is existed ", "Equipment", "equipment.bluetooth.name.exists");
                }
            }

            BeanConverter.copyProperties(equipmentDTO, equipment, true);
            SecurityUtils.checkMerchantPermission(userLoginInfo, equipment.getMerchant());
        } else {
            if (equipmentByIdNum.isPresent()) {
                throw new BadRequestAlertException("the IdentifyNumber is existed ", "Equipment", "equipment.upload.equipmentId.exists");
            }
            if (equipmentByImei.isPresent()) {
                throw new BadRequestAlertException("the IMEI is existed ", "Equipment", "equipment.upload.imei.exists");
            }
            if (StringUtils.isNotEmpty(equipmentDTO.getBluetoothName())) {
                Optional<Equipment> equipmentByBluetoothName = equipmentRepository.findOneByBluetoothName(equipmentDTO.getBluetoothName());
                if (equipmentByBluetoothName.isPresent()) {
                    throw new BadRequestAlertException("the Bluetooth Name is existed ", "Equipment", "equipment.bluetooth.name.exists");
                }
            }
            equipment = EquipmentService.toEntity(equipmentDTO);
            equipment.setMerchant(new Merchant(userLoginInfo.getMerchantId()));
        }
        equipment = equipmentRepository.save(equipment);
        return EquipmentService.toDto(equipment);
    }

    /**
     * Get all the equipment.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentDTO> findAll(Pageable pageable, EquipmentVM vm) {
        log.debug("Request to get all Equipment");
        UserLoginInfo userLoginInfo = SecurityUtils.getCurrentUserLoginInfo();
        QueryParams<Equipment> queryParams = new QueryParams<>();

        queryParams.and("merchant.id", Filter.Operator.eq, userLoginInfo.getMerchantId());

        queryParams.and("status", Filter.Operator.ne, EquipmentStatus.DELETED);

        if (StringUtils.isNotEmpty(vm.getIdentifyNumber())) {
            queryParams.and("identifyNumber", Filter.Operator.like, vm.getIdentifyNumber());
        }
        if (StringUtils.isNotEmpty(vm.getImei())) {
            queryParams.and("imei", Filter.Operator.like, vm.getImei());
        }
        if (StringUtils.isNotEmpty(vm.getSimCardNum())) {
            queryParams.and("simCardNum", Filter.Operator.like, vm.getSimCardNum());
        }
        if(vm.getIsOnline() != null){
            queryParams.and("isOnline", Filter.Operator.eq, vm.getIsOnline());
        }

        if (vm.getIsBounded() != null) {
            queryParams.and("vehicle", vm.getIsBounded() ? Filter.Operator.isNotNull : Filter.Operator.isNull, null);
        }
        return equipmentRepository.findAll(queryParams, pageable)
            .map(EquipmentService::toDto);
    }

    /**
     * Get one equipment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentDTO> findOne(Long id) {
        log.debug("Request to get Equipment : {}", id);
        return equipmentRepository.findById(id)
            .map(EquipmentService::toDto);
    }

    /**
     * Delete the equipment by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Equipment : {}", id);

        Optional<Equipment> equipmentOptional = equipmentRepository.findById(id);
        if (equipmentOptional.isPresent()) {
            Equipment equipment = equipmentOptional.get();
            SecurityUtils.checkMerchantPermission(equipment.getMerchant().getId());

            if (EquipmentStatus.BOUND.equals(equipment.getStatus())) {
                throw new BadRequestAlertException("the Equipment is bound to Vehicle already, please unbound before deletion", "Equipment", "equipment.delete.bound.already");
            }
            Vehicle vehicle = equipment.getVehicle();
            if (vehicle != null) {
                vehicle.setBounded(false);
                vehicleRepository.save(vehicle);
            }
//            equipment.setStatus(EquipmentStatus.DELETED);
//            equipment.setVehicle(null);
            equipmentFaultRepository.deleteByEquipment(equipment);
            operationRecordRepository.deleteByEquipment(equipment);
            equipmentRepository.deleteById(id);

        }

    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Equipment> findByLicensePlateNumber(String plateNumber) {
        log.debug("Request to get all Equipment by plateNumber: {}", plateNumber);
        if(StringUtils.isBlank(plateNumber)){
            return Optional.empty();
        }
        QueryParams<Equipment> queryParams = new QueryParams<>();
        queryParams.and("vehicle.licensePlateNumber", Filter.Operator.eq, plateNumber);
        return equipmentRepository.findOne(queryParams);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Equipment> findByBluetoothName(String bluetoothName) {
        log.debug("Request to get Equipment by bluetoothName: {}", bluetoothName);
        if(StringUtils.isBlank(bluetoothName)){
            return Optional.empty();
        }
        return equipmentRepository.findOneByBluetoothName(bluetoothName);
    }

    @Override
    public List<EquipmentDTO> findAllUnboundEquipments() {
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        return equipmentRepository.findAllByStatusAndMerchantId(EquipmentStatus.UNBOUND, loginInfo.getMerchantId())
            .stream().map(EquipmentService::toDto).collect(Collectors.toList());
    }

    @Override
    public void upload(UploadResponse response, List<EquipmentData> equipmentDataList) {
        UserLoginInfo loginInfo = SecurityUtils.getCurrentUserLoginInfo();
        List<Equipment> equipments = new ArrayList<>();

        for (EquipmentData equipmentData : equipmentDataList) {
            boolean hasError = false;

            if (equipmentRepository.findOneByImeiAndStatusNot(equipmentData.getImei(), EquipmentStatus.DELETED).isPresent()) {
                createResult("equipment.upload.imei.exists", equipmentData.getRowIdx(), false, response.getErrors());
                hasError = true;
            }
            if (equipmentRepository.findOneByIdentifyNumberAndStatusNot(equipmentData.getIdentifyNumber(), EquipmentStatus.DELETED).isPresent()) {
                createResult("equipment.upload.equipmentId.exists", equipmentData.getRowIdx(), false, response.getErrors());
                hasError = true;
            }
//            Optional<Division> divisionOptional = divisionRepository.findByNameAndStatus(equipmentData.getDivName(), RecordStatus.ACTIVE);
//            if (divisionOptional.isPresent()) {
//                List<Division> orgs = divisionOptional.get().getChildren().stream()
//                    .filter(division -> equipmentData.getOrgName().equalsIgnoreCase(division.getName()))
//                    .filter(division -> RecordStatus.ACTIVE.equals(division.getStatus()))
//                    .collect(Collectors.toList());
//                if (orgs.isEmpty()) {
//                    createResult("equipment.upload.division.not.exists", equipmentData.getRowIdx(), false, response.getErrors());
//                    hasError = true;
//                } else {
//                    org = orgs.get(0);
//                }
//            } else {
//                createResult("equipment.upload.division.not.exists", equipmentData.getRowIdx(), false, response.getErrors());
//                hasError = true;
//            }
            if (!hasError) {
                // createResult("excel.upload.success", equipmentData.getRowIdx(), true, results);
                Equipment equipment = new Equipment();
                equipment.setIdentifyNumber(equipmentData.getIdentifyNumber());
                equipment.setImei(equipmentData.getImei());
                equipment.setSimCardNum(equipmentData.getSimCardNum());
                equipment.setVersion(equipmentData.getVersion());
                equipment.setRemark(equipmentData.getRemark());
                equipment.setMerchant(new Merchant(loginInfo.getMerchantId()));
                equipment.setOnline(false);
                equipment.setStatus(EquipmentStatus.UNBOUND);
                equipments.add(equipment);
            } else {
                response.successToError();
            }
        }
        equipmentRepository.saveAll(equipments);
    }

    private void createResult(String msgKey, int rowIdx, boolean success, List<RowParseError> results) {
        String message = messageSource.getMessage(msgKey, new String[]{String.valueOf(rowIdx)}, LocaleContextHolder.getLocale());
        RowParseError result = new RowParseError();
//        result.setIsSuccess(success);
        result.setMsg(message);
        result.setRowNum((long) rowIdx);
        results.add(result);
    }

}
