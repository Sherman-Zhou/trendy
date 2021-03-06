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

public interface StaffService {
    Optional<Staff> activateRegistration(String key);

    Optional<UserDTO> completePasswordReset(String newPassword, String key);

    void requestPasswordReset(String mail, Boolean isAdmin);

    Optional<UserDTO> requestPasswordReset(Long userId);

    Optional<Staff> registerUserEmail(UserRegisterVM userDTO);

    void changeUserEmail(ChangeEmailVM userDTO);

    Staff createUser(UserDTO userDTO);

    void updateUser(String name, String email, String langKey, String address, String mobileNo);

    Optional<UserDTO> updateUser(UserDTO userDTO);

//    void deleteUser(String login);

    Optional<UserDTO> updateUserStatus(Long id, RecordStatus status);

    void deleteUser(Long id);

    void changePassword(String currentClearTextPassword, String newPassword);

    Page<UserDTO> getAllManagedUsers(Pageable pageable, UserVM userVM);

    Optional<UserDetailsDTO> getUserWithAuthoritiesByLogin(String login);

    Optional<UserDetailsDTO> getUserEmail(String login);

    Optional<Staff> getUserWithAuthorities(Long id);

    Optional<UserDetailsDTO> getUserWithAuthorities();

    Optional<Staff> getUserWithShopsAndCities();

    Optional<UserDetailsDTO> getSystemUserWithAuthorities(String login);

    List<RoleDTO> getRolesForMerchant();

    Optional<UserDetailsDTO> findOneByEmailIgnoreCase(String email);

    Optional<UserDetailsDTO> findOneByLogin(String login);

    void updateSystemUser(String name, String email, String langKey, String address, String mobileNo);

    List<Permission> findAllUserPermissionsByLogin(String login);

    UserDTO assignDivision(Long userId, List<String> divisionIds);


    List<String> findAllUserDivisionIds(Long userId);

    List<String> findAllUserDivisionIds(String login);

}
