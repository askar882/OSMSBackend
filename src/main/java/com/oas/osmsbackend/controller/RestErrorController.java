package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.response.ErrorResponse;
import com.oas.osmsbackend.util.RequestUtil;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 错误处理控制器。
 * 处理{@link javax.servlet.http.HttpServletResponse#sendError(int, String)}被调用表示的错误。
 * 目前只有{@link org.springframework.web.servlet.resource.ResourceHttpRequestHandler#handleRequest(HttpServletRequest, HttpServletResponse)}
 * 在未找到资源时会调用{@link HttpServletResponse#sendError(int, String)}方法。
 *
 * @author askar882
 * @date 2022/05/05
 */
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class RestErrorController implements ErrorController {
    /**
     * 错误属性。
     */
    private final ErrorAttributes errorAttributes;

    /**
     * 处理错误请求。
     *
     * @param request 请求。
     * @return {@link ErrorResponse}对象。
     */
    @RequestMapping
    public ErrorResponse error(HttpServletRequest request) {
        Map<String, Object> attributes = getErrorAttributes(request);
        Integer status = (Integer) attributes.get("status");
        String message = (String) attributes.get("error");
        log.debug("Error controller with error attributes '{}'.", attributes);
        HttpStatus httpStatus = RequestUtil.INSTANCE.getErrorStatus(request);
        if (status == null) {
            status = httpStatus.value();
        }
        if (!StringUtils.hasLength(message)) {
            message = httpStatus.getReasonPhrase();
        }
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .build();
    }

    /**
     * 提取错误属性。
     *
     * @param request 包含错误属性的请求。
     * @return 错误属性 {@link Map}。
     */
    private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        return this.errorAttributes.getErrorAttributes(new ServletWebRequest(request), ErrorAttributeOptions.defaults());
    }
}
