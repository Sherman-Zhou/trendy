package com.joinbe.repository;

import com.joinbe.domain.Role;
import com.joinbe.domain.enumeration.RecordStatus;
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


    @Query("select role from Role role left join fetch role.permissions where role.id =:id")
    Optional<Role> findOneWithEagerRelationships(@Param("id") Long id);

    Optional<Role> findByCodeAndStatusIs(String code, RecordStatus recordStatus);

    Optional<Role> findByNameAndStatusNot(String name, RecordStatus status);

    Optional<Role> findByCodeAndStatusNot(String code, RecordStatus recordStatus);

    List<Role> findAllByRoleTypeAndStatusAndMerchantId(String roleType, RecordStatus status, Long merchantId);

    @EntityGraph(attributePaths = {"permissions"})
    List<Role> findRoleWithPermissionsById(Long id);
}
