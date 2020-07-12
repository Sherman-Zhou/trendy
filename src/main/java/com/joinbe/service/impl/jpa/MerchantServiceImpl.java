package com.joinbe.service.impl.jpa;

import com.joinbe.common.util.Filter;
import com.joinbe.common.util.QueryParams;
import com.joinbe.domain.Merchant;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.repository.MerchantRepository;
import com.joinbe.service.MerchantService;
import com.joinbe.service.dto.MerchantDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("JpaMerchantService")
@Transactional
public class MerchantServiceImpl implements MerchantService {


    private final MerchantRepository merchantRepository;

    public MerchantServiceImpl(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public Page<MerchantDTO> findAll(Pageable pageable) {
        QueryParams<Merchant> queryParams = new QueryParams<>();
        queryParams.and("status", Filter.Operator.ne, RecordStatus.DELETED);
        return merchantRepository.findAll(queryParams, pageable)
            .map(MerchantService::toDto);
    }

    @Override
    public MerchantDTO save(MerchantDTO dto) {
        Merchant merchant;
        if (dto.getId() != null) {
            merchant = merchantRepository.getOne(dto.getId());
            merchant.setDescription(dto.getDescription());
            merchant.setName(dto.getName());
        } else {
            merchant = MerchantService.toEntity(dto);
        }
        merchant = merchantRepository.save(merchant);
        return MerchantService.toDto(merchant);
    }

    @Override
    public Optional<MerchantDTO> findOne(Long id) {
        return merchantRepository.findById(id).map(MerchantService::toDto);
    }

    @Override
    public List<Merchant> getAll() {
        return merchantRepository.findAllByStatusNot(RecordStatus.DELETED);
    }

    @Override
    public void delete(Long id) {
        Merchant merchant = merchantRepository.getOne(id);
        merchant.setStatus(RecordStatus.DELETED);
    }

    @Override
    public Optional<Merchant> findByName(String name) {
        return merchantRepository.findByNameAndStatusNot(name, RecordStatus.DELETED);
    }
}
