package com.github.rusichpt.Messenger.services.impl;

import com.github.rusichpt.Messenger.dto.UserProfile;
import com.github.rusichpt.Messenger.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    private final UserServiceImpl service;
    private final PasswordEncoder encoder;
    private final User user = new User(null, "test@mail.ru", "123",
            "username", "Pavel", "Tokarev", true, UUID.randomUUID().toString());

    @Autowired
    public UserServiceImplTest(UserServiceImpl service, PasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @Test
    void save() {
        User savedUser = service.createUser(user);
        user.setId(savedUser.getId());

        Assertions.assertEquals(user, savedUser);
    }

    @Test
    void checkUniqueFields() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.createUser(user);
            service.createUser(user);
        });
    }

    @Test
    void delete() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            User savedUser = service.createUser(user);
            service.deleteUser(savedUser);
            service.findUserById(savedUser.getId());
        });
    }

    @Test
    void updateUser() {
        User createdUser = service.createUser(user);
        UserProfile profile = createdUser.getProfile();
        profile.setUsername("testUsername");
        profile.setSurname("Ivanov");
        createdUser.setProfile(profile);

        User savedUser = service.updateUser(createdUser);

        Assertions.assertEquals(profile, savedUser.getProfile());
    }

    @Test
    void updateUserPassword() {
        User savedUser = service.createUser(user);
        String newPass = "321";

        service.updateUserPass(savedUser, newPass);
        User foundUser = service.findUserById(savedUser.getId());

        Assertions.assertTrue(encoder.matches(newPass, foundUser.getPassword()));
    }
}