package com.github.rusichpt.Messenger.services;

import com.github.rusichpt.Messenger.dto.UserProfile;
import com.github.rusichpt.Messenger.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDetails loadUserById(Long id) throws UsernameNotFoundException;

    User createUser(User user);

    User findUserById(Long id);

    User findUserByUsername(String username);

    List<User> findAllUsers();

    User updateUser(User user);

    UserProfile updateUserProfileById(Long id, UserProfile profile);

    void updateUserPasswordById(Long id, String password);


    void deleteUserById(Long id);
}
