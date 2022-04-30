package com.oas.osmsbackend.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author askar882
 * @date 2022/04/20
 */
@Data
@AllArgsConstructor
@Builder
@JsonRootName("error")
public class ErrorResponse {
    private int status;
    private String message;
}
