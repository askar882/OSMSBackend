package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.annotaion.CurrentUser;
import com.oas.osmsbackend.annotaion.HasRole;
import com.oas.osmsbackend.entity.User;
import com.oas.osmsbackend.enums.Role;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.security.JwtTokenProvider;
import com.oas.osmsbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Tag(name = "身份认证控制器", description = "处理用户身份认证请求，实现登录和注册。")
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
    @Operation(summary = "登录", description = "使用提供的用户名密码尝试登录，登录成功生成Token返回，失败抛出异常。")
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
     * 登出当前帐号或管理员将在线用户踢下线。
     *
     * @param userId 登出的用户的ID。
     */
    @DeleteMapping("logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CurrentUser
    @HasRole(Role.ADMIN)
    @Operation(summary = "登出", description = "主动登出或管理员将用户踢下线。")
    public void logout(Long userId) {

    }
}
