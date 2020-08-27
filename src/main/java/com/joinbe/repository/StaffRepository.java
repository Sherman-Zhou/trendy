package com.joinbe.repository;

import com.joinbe.domain.Staff;
import com.joinbe.domain.enumeration.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Staff} entity.
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<Staff> findOneByActivationKey(String activationKey);

//    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<Staff> findOneByResetKey(String resetKey);

    @EntityGraph(attributePaths = {"cities", "shops"})
    Optional<Staff> findOneByEmailIgnoreCaseAndStatusNot(String email, RecordStatus status);

    @EntityGraph(attributePaths = {"cities", "shops"})
    Optional<Staff> findOneByLoginAndStatusNot(String login, RecordStatus status);


    @EntityGraph(attributePaths = {"roles"})
    Optional<Staff> findOneWithRolesById(Long id);

    @EntityGraph(attributePaths = {"cities", "shops"})
    Optional<Staff> findOneWithShopsAndCitiesByLogin(String login);

    @EntityGraph(attributePaths = {"roles"})
        //@Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<Staff> findOneWithRolesByLogin(String login);

    @EntityGraph(attributePaths = {"roles"})
        //@Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<Staff> findOneWithRolesByEmailIgnoreCase(String email);

    Page<Staff> findAllByLoginNot(Pageable pageable, String login);

    @EntityGraph(attributePaths = {"roles"})
    Optional<Staff> findOneWithRolesByLoginAndStatus(String login, RecordStatus recordStatus);

    @EntityGraph(attributePaths = {"shops", "cities"})
    Optional<Staff> findOneWithDivisionsById(Long id);


    @Query("select user from Staff user join user.roles roles where roles.id =:roleId and user.status <> 'D'")
    List<Staff> findUsersByRoleId(@Param("roleId") Long roleId);

    @EntityGraph(attributePaths = {"cities", "shops"})
    Optional<Staff> findOneWithDivisionsByLogin(String login);

    @Query("select user from Staff user join user.roles roles join user.merchant m where roles.code =:roleCode and user.status <> 'D' and m.id =:merchantId")
    List<Staff> findMerchantAdmin(@Param("roleCode") String roleCode, @Param("merchantId") Long merchantId);
}
