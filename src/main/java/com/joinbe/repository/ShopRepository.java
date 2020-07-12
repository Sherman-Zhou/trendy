package com.joinbe.repository;

import com.joinbe.domain.Merchant;
import com.joinbe.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data  repository for the Shop entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShopRepository extends JpaRepository<Shop, String>,
    JpaSpecificationExecutor<Shop> {

    Set<Shop> findByMerchant(Merchant merchant);

    Set<Shop> findByMerchantId(Long merchantId);

    List<Shop> findByCityId(String cityId);
}
