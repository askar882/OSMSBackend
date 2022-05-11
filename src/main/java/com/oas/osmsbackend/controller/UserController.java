package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.annotaion.IsAdmin;
import com.oas.osmsbackend.annotaion.IsSelf;
import com.oas.osmsbackend.domain.AuthenticationRequest;
import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.service.UserService;
import com.oas.osmsbackend.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public DataResponse addUser(@RequestBody AuthenticationRequest authenticationRequest) {
        return new DataResponse() {{
            put("user", userService.register(authenticationRequest));
        }};
    }

    @GetMapping
    @IsAdmin
    public DataResponse listUser() {
        return new DataResponse() {{
            put("users", userRepository.findAll());
        }};
    }

    @GetMapping("/{userId}")
    @IsSelf
    public DataResponse getUser(@PathVariable Long userId, Authentication authentication) {
        if (!RequestUtil.INSTANCE.isSelf(userId, (UserDetails) authentication.getPrincipal())) {
            throw new AccessDeniedException("Access denied.");
        }
        return new DataResponse() {{
            put("user", userRepository.findById(userId));
        }};
    }
}
