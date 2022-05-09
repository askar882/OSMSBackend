package com.oas.osmsbackend.handler;

import com.oas.osmsbackend.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpStatus;

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
        log.debug("Writing error report with status {}.", statusCode);
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
        ResponseUtil.INSTANCE.writeError(response, statusCode, message);
    }
}
