package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.annotaion.CurrentUser;
import com.oas.osmsbackend.annotaion.HasRole;
import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.enums.Role;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@HasRole(Role.ADMIN)
@Tag(name = "用户管理控制器", description = "处理管理用户的请求，当前用户有ADMIN角色时可调用所有方法。")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "添加用户", description = "添加一个新的用户到数据库，如果用户存在则抛出异常。只有ADMIN角色用户可调用。")
    public DataResponse create(@RequestBody User user) {
        return new DataResponse() {{
            put("user", userService.create(user));
        }};
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "列取用户", description = "获取数据库里的所有用户。只有ADMIN角色用户可调用。")
    public DataResponse list() {
        return new DataResponse() {{
            put("users", userService.list());
        }};
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @CurrentUser
    @Operation(summary = "获取用户数据",
            description = "获取用户ID为userId的用户的数据。ADMIN角色用户可调用，非ADMIN角色用户仅可获取自己的用户数据。")
    public DataResponse read(@PathVariable Long userId) throws AccountNotFoundException {
        return new DataResponse() {{
            put("user", userService.read(userId));
        }};
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @CurrentUser
    @Operation(summary = "更新用户数据",
            description = "更新用户ID为userId的用户的数据，用户ID不可变，username不能与现存用户冲突。" +
                    "ADMIN角色用户可调用，非ADMIN角色用户仅可更新自己的用户数据，不可修改角色。")
    public DataResponse update(@PathVariable Long userId, @RequestBody User user) throws AccountNotFoundException {
        return new DataResponse() {{
            put("user", userService.update(userId, user));
        }};
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除用户", description = "删除用户ID为userId的用户。只有ADMIN角色用户可调用。")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
