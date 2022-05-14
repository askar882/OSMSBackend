package com.oas.osmsbackend.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 错误响应数据。
 * @author askar882
 * @date 2022/04/20
 */
@Data
@AllArgsConstructor
@Builder
@JsonRootName("error")
@JsonTypeName("error")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class ErrorResponse {
    private int status;
    private String message;
}
