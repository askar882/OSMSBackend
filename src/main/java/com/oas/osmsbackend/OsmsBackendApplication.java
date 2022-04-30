package com.oas.osmsbackend;

import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author askar882
 * @date 2022/04/28
 */
@SpringBootApplication
@Slf4j
public class OsmsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OsmsBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            log.debug("Initializing user data...");
            Arrays.asList(
                    Arrays.asList("admin", "admin", "USER,ADMIN"),
                    Arrays.asList("askar", "askar", "USER")
            ).forEach(data -> {
                userRepository.saveAndFlush(User.builder()
                        .username(data.get(0))
                        .password(passwordEncoder.encode(data.get(1)))
                        .roles(
                                Arrays.stream(data.get(2).split(","))
                                        .map(s -> "ROLE_" + s)
                                        .collect(Collectors.toList())
                        ).build());
            });
            log.debug("Printing all users...");
            userRepository
                    .findAll()
                    .stream().map(User::toString)
                    .forEach(log::debug);
        };
    }
}
