package com.oas.osmsbackend.controller;

import com.oas.osmsbackend.entity.Dealer;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.service.DealerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 经销商控制器。
 *
 * @author askar882
 * @date 2022/05/19
 */
@RestController
@RequestMapping("/dealers")
@RequiredArgsConstructor
@Tag(name = "经销商控制器", description = "处理管理经销商的请求")
public class DealerController {
    private final DealerService dealerService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "添加经销商")
    public DataResponse create(@RequestBody Dealer dealer) {
        return dealerService.create(dealer);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "列取经销商")
    public DataResponse list(Pageable pageable) {
        return dealerService.list(pageable);
    }

    @GetMapping("/{dealerId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "获取经销商数据")
    public DataResponse read(@PathVariable Long dealerId) {
        return dealerService.read(dealerId);
    }

    @PutMapping("/{dealerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "更新经销商")
    public DataResponse update(@PathVariable Long dealerId, @RequestBody Dealer dealer) {
        return dealerService.update(dealerId, dealer);
    }

    @DeleteMapping("/{dealerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除经销商")
    public void delete(@PathVariable Long dealerId) {
        dealerService.delete(dealerId);
    }
}
