package com.joinbe.repository;

import com.joinbe.domain.Role;
import com.joinbe.domain.enumeration.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Role} entity.
 */
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    @Query(value = "select distinct role from Role role left join fetch role.permissions",
        countQuery = "select count(distinct role) from Role role")
    Page<Role> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct role from Role role left join fetch role.permissions")
    List<Role> findAllWithEagerRelationships();

    @Query("select role from Role role left join fetch role.permissions where role.id =:id")
    Optional<Role> findOneWithEagerRelationships(@Param("id") Long id);

    Optional<Role> findByCodeAndStatusIs(String code, RecordStatus recordStatus);

    List<Role> findAllByStatus(RecordStatus status);

    @EntityGraph(attributePaths = {"permissions"})
    List<Role> findRoleWithPermissionsById(Long id);
}
