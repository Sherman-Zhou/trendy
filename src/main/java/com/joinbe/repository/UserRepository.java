package com.joinbe.repository;

import com.joinbe.domain.User;
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
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<User> findOneByActivationKey(String activationKey);

//    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCaseAndStatusNot(String email, RecordStatus status);

    Optional<User> findOneByLoginAndStatusNot(String login, RecordStatus status);


    @EntityGraph(attributePaths = {"roles", "divisions"})
    Optional<User> findOneWithRolesById(Long id);

    @EntityGraph(attributePaths = {"roles", "divisions"})
        //@Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<User> findOneWithRolesByLogin(String login);

    @EntityGraph(attributePaths = {"roles", "divisions"})
        //@Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithRolesByEmailIgnoreCase(String email);

    Page<User> findAllByLoginNot(Pageable pageable, String login);

    @EntityGraph(attributePaths = {"roles", "divisions"})
    Optional<User> findOneWithRolesByLoginAndStatus(String login, RecordStatus recordStatus);

    @EntityGraph(attributePaths = {"divisions"})
    Optional<User> findOneWithDivisionsById(Long id);

    @Query("select user from User user join user.roles roles where roles.id =:roleId and user.status <> 'D'")
    List<User> findUsersByRoleId(@Param("roleId") Long roleId);

    @EntityGraph(attributePaths = {"divisions"})
    Optional<User> findOneWithDivisionsByLogin(String login);
}
