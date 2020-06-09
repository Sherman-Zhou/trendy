package com.joinbe.repository;

import com.joinbe.domain.User;
import com.joinbe.domain.enumeration.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

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
}
