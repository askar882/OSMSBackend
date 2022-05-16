package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.annotaion.CurrentUser;
import com.oas.osmsbackend.annotaion.HasRole;
import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse create(@RequestBody User user) {
        return new DataResponse() {{
            put("user", userService.create(user));
        }};
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DataResponse list() {
        return new DataResponse() {{
            put("users", userService.list());
        }};
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @CurrentUser
    public DataResponse read(@PathVariable Long userId) throws AccountNotFoundException {
        return new DataResponse() {{
            put("user", userService.read(userId));
        }};
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @CurrentUser
    public DataResponse update(@PathVariable Long userId, @RequestBody User user) throws AccountNotFoundException {
        return new DataResponse() {{
            put("user", userService.update(userId, user));
        }};
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CurrentUser
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
