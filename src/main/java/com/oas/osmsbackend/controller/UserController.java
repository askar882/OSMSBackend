package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.annotaion.IsAdmin;
import com.oas.osmsbackend.annotaion.IsSelf;
import com.oas.osmsbackend.domain.AuthenticationRequest;
import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.service.UserService;
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
 * @author askar882
 * @date 2022/05/01
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse addUser(@RequestBody AuthenticationRequest authenticationRequest) {
        return new DataResponse() {{
            put("user", userService.register(authenticationRequest));
        }};
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @IsAdmin
    public DataResponse listUser() {
        return new DataResponse() {{
            put("users", userRepository.findAll());
        }};
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @IsSelf
    public DataResponse getUser(@PathVariable Long userId) throws AccountNotFoundException {
        return new DataResponse() {{
            put("user", userRepository.findById(userId).orElseThrow(() -> new AccountNotFoundException("Not found")));
        }};
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @IsSelf
    public DataResponse updateUser(@PathVariable Long userId, @RequestBody User user) {
        // FIXME: Implement.
        return new DataResponse() {{
            put("user", userRepository.saveAndFlush(user));
        }};
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @IsSelf
    public void deleteUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @IsSelf
    public DataResponse patchUser(@PathVariable Long userId, @RequestBody User user) {
        // TODO: Implement.
        return new DataResponse() {{
            put("user", userService.patchUser(user));
        }};
    }
}
