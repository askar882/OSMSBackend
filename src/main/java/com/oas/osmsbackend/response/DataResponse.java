package com.oas.osmsbackend.response;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashMap;

/**
 * @author askar882
 * @date 2022/05/01
 */
@JsonRootName("data")
public class DataResponse extends HashMap<String, Object> {
}
