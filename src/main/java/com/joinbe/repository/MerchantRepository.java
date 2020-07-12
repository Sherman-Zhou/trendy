package com.joinbe.repository;


import com.joinbe.domain.Merchant;
import com.joinbe.domain.enumeration.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long>, JpaSpecificationExecutor<Merchant> {

    Optional<Merchant> findByNameAndStatusNot(String name, RecordStatus status);

    List<Merchant> findAllByStatusNot(RecordStatus status);
}
