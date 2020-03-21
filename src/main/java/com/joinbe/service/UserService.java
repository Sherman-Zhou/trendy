package com.joinbe.service;

import com.joinbe.domain.User;
import com.joinbe.service.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> activateRegistration(String key);

    Optional<User> completePasswordReset(String newPassword, String key);

    Optional<User> requestPasswordReset(String mail);

    User registerUser(UserDTO userDTO, String password);

    User createUser(UserDTO userDTO);

    void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl);

    Optional<UserDTO> updateUser(UserDTO userDTO);

    void deleteUser(String login);

    void changePassword(String currentClearTextPassword, String newPassword);

    @Transactional(readOnly = true)
    Page<UserDTO> getAllManagedUsers(Pageable pageable);

    @Transactional(readOnly = true)
    Optional<User> getUserWithAuthoritiesByLogin(String login);

    @Transactional(readOnly = true)
    Optional<User> getUserWithAuthorities(Long id);

    @Transactional(readOnly = true)
    Optional<User> getUserWithAuthorities();

    @Scheduled(cron = "0 0 1 * * ?")
    void removeNotActivatedUsers();

    List<String> getAuthorities();
}
