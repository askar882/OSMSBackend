package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.domain.AuthenticationRequest;
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

    @PostMapping("login")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse login(@RequestBody AuthenticationRequest requestData) {
        String username = requestData.getUsername();
        String password = requestData.getPassword();
        log.debug("Login with credentials: {username: '{}', password: '{}'}", username, password);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
        String token = jwtTokenProvider.createToken(authentication);
        return new DataResponse() {{
                put("token", token);
        }};
    }

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse register(@RequestBody AuthenticationRequest authenticationRequest) {
        User user = userService.register(authenticationRequest);
        return new DataResponse() {{
            put("user", user.toString());
        }};
    }

    @PostMapping("refresh")
    public String refresh() {
        return "";
    }
}
