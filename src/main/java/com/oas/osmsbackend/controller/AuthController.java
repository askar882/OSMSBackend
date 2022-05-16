package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.security.JwtTokenProvider;
import com.oas.osmsbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 身份认证控制器，用于用户登录和注册。
 *
 * @author askar882
 * @date 2022/03/31
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * 用户登录，成功返回Token，失败抛异常。
     *
     * @param user 登录的用户。
     * @return 包含Token的 {@link DataResponse}实例。
     */
    @PostMapping("login")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse login(@RequestBody User user) {
        log.debug("Login with user: '{}'.", user);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        String token = jwtTokenProvider.createToken(authentication);
        return new DataResponse() {{
                put("token", token);
        }};
    }

    /**
     * 注册用户，成功返回创建的{@link User}实例，失败抛异常。
     *
     * @param user 注册的用户信息。
     * @return 包含注册成功的 {@link User}实例的{@link DataResponse}。
     */
    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse register(@RequestBody User user) {
        return new DataResponse() {{
            put("user", userService.create(user));
        }};
    }
}
