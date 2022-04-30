package com.oas.osmsbackend.handler;

import com.oas.osmsbackend.response.ErrorResponse;
import com.oas.osmsbackend.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * @author askar882
 * @date 2022/05/01
 */
@Slf4j
public class ErrorReport extends ErrorReportValve {
    @Override
    protected void report(Request request, Response response, Throwable throwable) {
        int statusCode = response.getStatus();
        if (statusCode < HttpStatus.BAD_REQUEST.value()
                || response.getContentWritten() > 0
                || !response.setErrorReported()) {
            return;
        }
        String message = response.getMessage();
        if (message == null) {
            StringManager stringManager = StringManager.getManager(
                    "org.apache.catalina.valves",
                    request.getLocales()
            );
            try {
                message = stringManager.getString("http." + statusCode + ".reason");
            } catch (Exception e) {
                log.debug("Error retrieving reason string for status {}: {}", statusCode, e.getMessage());
            }
            if (message == null) {
                message = stringManager.getString("errorReportValve.unknownReason");
            }
            response.setLocale(stringManager.getLocale());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(statusCode)
                .message(message)
                .build();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getReporter().write(JsonUtil.getInstance().toJson(errorResponse));
            response.finishResponse();
        } catch (IOException e) {
            log.error("Error report write failed: {}", e.getMessage());
        }
    }
}
