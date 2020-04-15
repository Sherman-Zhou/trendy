package com.joinbe.repository;

import com.joinbe.domain.DictEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the DictEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DictEntryRepository extends JpaRepository<DictEntry, Long>,
    JpaSpecificationExecutor<DictEntry> {
}
