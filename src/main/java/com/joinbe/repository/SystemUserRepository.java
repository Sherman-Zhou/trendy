package com.joinbe.repository;

import com.joinbe.domain.Staff;
import com.joinbe.domain.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Staff} entity.
 */
@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long>, JpaSpecificationExecutor<SystemUser> {

    //@EntityGraph(attributePaths = {"role"})
    Optional<SystemUser> findOneWithRoleByLogin(String login);

}
