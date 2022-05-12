package com.oas.osmsbackend.annotaion;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 指定允许访问方法的用户的角色。标注一个类时应用于该类的所有方法。如果既标注类也标注该类的方法，则方法标注会覆盖类标注。
 * 优先级最高。
 * @author askar882
 * @date 2022/05/12
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface HasRole {
    /**
     * 授权的用户角色列表。
     */
    String[] value();
}
