package com.joinbe.service;

import com.joinbe.domain.Permission;
import com.joinbe.domain.User;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.service.dto.RoleDTO;
import com.joinbe.service.dto.UserDTO;
import com.joinbe.service.dto.UserDetailsDTO;
import com.joinbe.web.rest.vm.UserRegisterVM;
import com.joinbe.web.rest.vm.UserVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> activateRegistration(String key);

    Optional<User> completePasswordReset(String newPassword, String key);

    Optional<User> requestPasswordReset(String mail);

    Optional<User> requestPasswordReset(Long userId);

    Optional<User> registerUserEmail(UserRegisterVM userDTO);

    User createUser(UserDTO userDTO);

    void updateUser(String name, String email, String langKey, String imageUrl);

    Optional<UserDTO> updateUser(UserDTO userDTO);

//    void deleteUser(String login);

    Optional<UserDTO> updateUserStatus(Long id, RecordStatus status);

    void deleteUser(Long id);

    void changePassword(String currentClearTextPassword, String newPassword);

    Page<UserDTO> getAllManagedUsers(Pageable pageable, UserVM userVM);

    Optional<User> getUserWithAuthoritiesByLogin(String login);

    Optional<User> getUserWithAuthorities(Long id);

    Optional<UserDetailsDTO> getUserWithAuthorities();

    List<RoleDTO> getRoles();

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByLogin(String login);

    List<Permission> findAllUserPermissionsByLogin(String login);

    UserDTO assignDivision(Long userId, List<Long> divisionIds);

}
