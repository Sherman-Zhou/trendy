package com.joinbe.repository;

import com.joinbe.domain.DictType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the DictType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DictTypeRepository extends JpaRepository<DictType, Long>,
    JpaSpecificationExecutor<DictType> {
}
