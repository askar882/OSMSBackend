package com.oas.osmsbackend.security;

import com.oas.osmsbackend.config.AppConfiguration;
import com.oas.osmsbackend.entity.User;
import com.oas.osmsbackend.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Redis存储辅助类，用于管理JWT Token和{@link User}之间的联系。
 *
 * @author askar882
 * @date 2022/05/28
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStore {
    private static final String ID_KEY_PREFIX = "ID_";
    private static final String TOKEN_KEY_PREFIX = "JWT_";
    private final AppConfiguration appConfiguration;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 保存JWT Token到Redis。为了方便查询，分别保存username对应Token和Token对应{@link User}对象。
     *
     * @param user  需要记录的{@link User}对象。
     * @param token JWT Token。
     */
    public void saveToken(User user, String token) {
        // 一个用户只能有一个Token
        deleteToken(user.getId());
        redisTemplate.opsForValue().set(ID_KEY_PREFIX + user.getId(),
                token,
                appConfiguration.getTokenValidity());
        redisTemplate.opsForValue().set(TOKEN_KEY_PREFIX + token,
                JsonUtil.INSTANCE.toJson(user, false),
                appConfiguration.getTokenValidity());
        log.debug("Saved JWT token for user '{}'.", user.getUsername());
    }

    /**
     * 删除username对应的Token，将用户踢下线时使用。
     *
     * @param userId 需要踢掉的用户的用户名。
     */
    public void deleteToken(Long userId) {
        String key = ID_KEY_PREFIX + userId;
        String token = redisTemplate.opsForValue().get(key);
        if (token != null) {
            redisTemplate.delete(key);
            redisTemplate.delete(TOKEN_KEY_PREFIX + token);
            log.debug("Deleted token data for user '{}'.", userId);
        }
    }

    /**
     * 从Redis获取{@code token}对应的{@link User}实例。
     *
     * @param token 查找的Token。
     * @return {@link User}实例的{@link Optional}对象。
     */
    public Optional<User> getUser(String token) {
        return JsonUtil.INSTANCE.fromJson(redisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + token), User.class);
    }
}
