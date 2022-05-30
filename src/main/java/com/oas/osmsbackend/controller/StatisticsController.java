package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 统计控制器。
 *
 * @author askar882
 * @date 2022/05/30
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "统计控制器", description = "返回统计数据")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "获取统计数据")
    public DataResponse get(@RequestParam String name, @RequestParam Optional<Long> top) {
        return statisticsService.get(name, top);
    }
}
