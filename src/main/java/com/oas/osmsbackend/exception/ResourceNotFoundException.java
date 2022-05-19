package com.oas.osmsbackend.exception;

/**
 * 资源未找到时抛出的异常。
 *
 * @author askar882
 * @date 2022/05/12
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
