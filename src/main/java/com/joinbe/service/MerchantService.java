package com.joinbe.service;

import com.joinbe.common.util.BeanConverter;
import com.joinbe.domain.Merchant;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.service.dto.MerchantDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MerchantService {

    static MerchantDTO toDto(Merchant merchant) {

        MerchantDTO dto = BeanConverter.toDto(merchant, MerchantDTO.class);
        dto.setStatus(merchant.getStatus().getCode());
        return dto;
    }

    static Merchant toEntity(MerchantDTO dto) {
        Merchant role = BeanConverter.toEntity(dto, Merchant.class);
        role.setStatus(RecordStatus.resolve(dto.getStatus()));
        return role;
    }

    Page<MerchantDTO> findAll(Pageable pageable);

    MerchantDTO save(MerchantDTO dto);

    Optional<MerchantDTO> findOne(Long id);

    List<Merchant> getAll();

    void delete(Long id);

    Optional<Merchant> findByName(String name);
}
