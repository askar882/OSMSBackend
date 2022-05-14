package com.oas.osmsbackend.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.HashMap;

/**
 * 正常响应数据。
 * @author askar882
 * @date 2022/05/01
 */
@JsonRootName("data")
@JsonTypeName("data")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class DataResponse extends HashMap<String, Object> {
}
