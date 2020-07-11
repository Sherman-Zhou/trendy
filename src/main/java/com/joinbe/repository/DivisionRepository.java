package com.joinbe.repository;

import com.joinbe.domain.Division;
import com.joinbe.domain.enumeration.RecordStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Division entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DivisionRepository extends JpaRepository<Division, Long>, JpaSpecificationExecutor<Division> {

    @EntityGraph(attributePaths = {"parent", "children"})
    List<Division> findAllByParentId(Long parentId);

    @EntityGraph(attributePaths = {"children"})
    List<Division> findAllRootDeptByParentIsNull();

    @Query("select distinct d from Staff u join u.divisions d where u.login =:login and d.status ='A'")
    List<Division> findAllByUserLogin(@Param("login") String login);

    Optional<Division> findByNameAndStatus(String name, RecordStatus status);
}
