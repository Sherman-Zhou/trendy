package com.joinbe.repository;

import com.joinbe.domain.City;
import com.joinbe.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Spring Data  repository for the City entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityRepository extends JpaRepository<City, String>,
    JpaSpecificationExecutor<City> {

    Set<City> findByMerchant(Merchant merchant);

    Set<City> findByMerchantId(Long merchantId);
}
