package com.joinbe.repository;

import com.joinbe.domain.Permission;
import com.joinbe.domain.enumeration.RecordStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Permission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>,
    JpaSpecificationExecutor<Permission> {

    @Query("select distinct perm from Staff u join u.roles r join r.permissions perm where u.login =:login and r.status ='A' and perm.status ='A'")
    List<Permission> findAllByUserLogin(@Param("login") String login);

    @Query("select distinct perm from SystemUser u join u.role r join r.permissions perm where u.login =:login and r.status ='A' and perm.status ='A'")
    List<Permission> findAllBySystemUserLogin(@Param("login") String login);

    @EntityGraph(attributePaths = "children")
    List<Permission> findAllByStatus(RecordStatus status);

    @EntityGraph(attributePaths = {"parent"})
    List<Permission> findAllPermsByStatusOrderBySortOrder(RecordStatus status);

}
