package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.response.ErrorResponse;
import com.oas.osmsbackend.util.RequestUtil;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author askar882
 * @date 2022/05/05
 */
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class RestErrorController implements ErrorController {
    @RequestMapping
    public ErrorResponse error(HttpServletRequest request) {
        HttpStatus status = RequestUtil.INSTANCE.getErrorStatus(request);
        return ErrorResponse.builder()
                .status(status.value())
                .message(status.getReasonPhrase())
                .build();
    }
}
