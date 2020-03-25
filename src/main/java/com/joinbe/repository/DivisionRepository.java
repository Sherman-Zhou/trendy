package com.joinbe.repository;

import com.joinbe.domain.Division;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Division entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DivisionRepository extends JpaRepository<Division, Long>, JpaSpecificationExecutor<Division> {

    @EntityGraph(attributePaths = {"parent","children"})
    List<Division> findAllByParentId(Long parentId);

    @EntityGraph(attributePaths = {"children"})
    List<Division> findAllRootDeptByParentIsNull();
}
