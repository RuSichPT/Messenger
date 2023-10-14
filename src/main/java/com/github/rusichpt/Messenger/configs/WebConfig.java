package com.github.rusichpt.Messenger.configs;

import com.github.rusichpt.Messenger.models.User;
import com.github.rusichpt.Messenger.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
@Slf4j
public class WebConfig {
    @Bean
    public CommandLineRunner dataLoader(UserService userService) {
        return args -> {
            User user1 = new User(null, "user1@mail.ru", "123",
                    "user1", "Pavel", "Tokarev", true, UUID.randomUUID().toString());
            User user2 = new User(null, "user2@mail.ru", "321",
                    "user2", "Alex", "Firov", true, UUID.randomUUID().toString());
            userService.createUser(user1);
            userService.createUser(user2);
            log.info("DataLoader loaded data");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
