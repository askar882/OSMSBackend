package com.oas.osmsbackend.annotaion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定只有本人允许访问被标注的方法。适用于REST风格获取、更新和删除请求。
 * 优先级低于
 * @author askar882
 * @date 2022/05/11
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CurrentUser {
}
