package com.joinbe.service;

import com.joinbe.domain.Permission;
import com.joinbe.domain.Staff;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.service.dto.RoleDTO;
import com.joinbe.service.dto.UserDTO;
import com.joinbe.service.dto.UserDetailsDTO;
import com.joinbe.web.rest.vm.ChangeEmailVM;
import com.joinbe.web.rest.vm.UserRegisterVM;
import com.joinbe.web.rest.vm.UserVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<Staff> activateRegistration(String key);

    Optional<Staff> completePasswordReset(String newPassword, String key);

    Optional<Staff> requestPasswordReset(String mail);

    Optional<Staff> requestPasswordReset(Long userId);

    Optional<Staff> registerUserEmail(UserRegisterVM userDTO);

    Optional<Staff> changeUserEmail(ChangeEmailVM userDTO);

    Staff createUser(UserDTO userDTO);

    void updateUser(String name, String email, String langKey, String address, String mobileNo);

    Optional<UserDTO> updateUser(UserDTO userDTO);

//    void deleteUser(String login);

    Optional<UserDTO> updateUserStatus(Long id, RecordStatus status);

    void deleteUser(Long id);

    void changePassword(String currentClearTextPassword, String newPassword);

    Page<UserDTO> getAllManagedUsers(Pageable pageable, UserVM userVM);

    Optional<UserDetailsDTO> getUserWithAuthoritiesByLogin(String login);

    Optional<Staff> getUserWithAuthorities(Long id);

    Optional<UserDetailsDTO> getUserWithAuthorities();

    List<RoleDTO> getRoles();

    Optional<Staff> findOneByEmailIgnoreCase(String email);

    Optional<Staff> findOneByLogin(String login);

    List<Permission> findAllUserPermissionsByLogin(String login);

    UserDTO assignDivision(Long userId, List<Long> divisionIds);

    List<Long> findAllUserDivisionIds(Long userId);

    List<Long> findAllUserDivisionIds(String login);

}
