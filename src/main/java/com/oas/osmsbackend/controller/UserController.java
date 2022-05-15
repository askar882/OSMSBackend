package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.annotaion.CurrentUser;
import com.oas.osmsbackend.annotaion.HasRole;
import com.oas.osmsbackend.domain.AuthenticationRequest;
import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.response.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;

/**
 * 用户控制器。
 *
 * @author askar882
 * @date 2022/05/01
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@HasRole("ADMIN")
public class UserController {
    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse create(@RequestBody AuthenticationRequest authenticationRequest) {
        return new DataResponse() {{
            put("user", userRepository.saveAndFlush(User.builder()
                    .username(authenticationRequest.getUsername())
                    .password(authenticationRequest.getPassword())
                    .build()));
        }};
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DataResponse list() {
        return new DataResponse() {{
            put("users", userRepository.findAll());
        }};
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @CurrentUser
    public DataResponse read(@PathVariable Long userId) throws AccountNotFoundException {
        return new DataResponse() {{
            put("user", userRepository.findById(userId).orElseThrow(
                    () -> new AccountNotFoundException("User with ID " + userId + " is not found.")));
        }};
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @CurrentUser
    public DataResponse update(@PathVariable Long userId, @RequestBody User user) {
        // FIXME: Implement.
        return new DataResponse() {{
            put("user", userRepository.saveAndFlush(user));
        }};
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CurrentUser
    public void delete(@PathVariable Long userId) {
        log.debug("Delete user {}.", userId);
        userRepository.deleteById(userId);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @CurrentUser
    public DataResponse patch(@PathVariable Long userId, @RequestBody User user) {
        // TODO: Implement.
        return new DataResponse() {{
            put("user", user);
        }};
    }
}
