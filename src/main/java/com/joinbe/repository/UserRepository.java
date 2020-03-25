package com.joinbe.repository;

import com.joinbe.domain.User;
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

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByLogin(String login);


    @EntityGraph(attributePaths = "roles")
    Optional<User> findOneWithRolesById(Long id);

    @EntityGraph(attributePaths = "roles")
        //@Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<User> findOneWithRolesByLogin(String login);

    @EntityGraph(attributePaths = "roles")
        //@Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithRolesByEmailIgnoreCase(String email);

    Page<User> findAllByLoginNot(Pageable pageable, String login);
}
