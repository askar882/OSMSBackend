package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.Order;
import com.oas.osmsbackend.entity.Product;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.OrderRepository;
import com.oas.osmsbackend.response.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统计服务。
 *
 * @author askar882
 * @date 2022/05/30
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {
    private final OrderRepository orderRepository;

    /**
     * 生成指定的数据并返回。
     *
     * @param name 统计数据的名称，目前支持["dealer", "products"]。
     * @param top 若不为null，只返回{@code top}组数据，否则返回所有数据。
     * @return 统计的数据。
     */
    public DataResponse get(String name, Optional<Long> top) {
        log.debug("Getting top '{}' statistics on '{}'.", top, name);
        Function<List<Map<String, Object>>, List<Map<String, Object>>> slice = list ->
                top.map(t -> list.stream()
                        .limit(t).collect(Collectors.toList()))
                        .orElse(list);
        switch (name) {
            case "dealers":
                return new DataResponse() {{
                    put("dealers", slice.apply(dealersMarketShare()));
                }};
            case "products":
                return new DataResponse() {{
                    put("products", slice.apply(productsMarketShare()));
                }};
            case "orderStates":
                return new DataResponse() {{
                    put("states", orderState());
                }};
            default:
                throw new ResourceNotFoundException("未找到指定的资源");
        }
    }

    /**
     * 查找所有有订单记录的产品，销售额由高到低排序并返回。
     *
     * @return 返回一个框架为[{ product: Product, count: Long, expense: Double }, ...]的Map列表。
     */
    public List<Map<String, Object>> productsMarketShare() {
        log.debug("Generating products market share.");
        List<Order> orders = orderRepository.findAll();
        if (CollectionUtils.isEmpty(orders)) {
            return Collections.emptyList();
        }
        // { productId: { product: Product, count: Long, expense: Double } }
        Map<Long, Map<String, Object>> marketShare = new HashMap<>();
        orders.forEach(order -> order.getOrderItems()
                .forEach(item -> {
                    Product product = item.getProduct();
                    Long productId = product.getId();
                    Long count = Long.valueOf(item.getCount());
                    Double price = item.getPrice();
                    if (marketShare.containsKey(productId)) {
                        Map<String, Object> productMap = marketShare.get(productId);
                        productMap.put("count", (Long) productMap.get("count") + count);
                        productMap.put("expense", (Double) productMap.get("expense") + price * count);
                    } else {
                        marketShare.put(productId, new HashMap<String, Object>() {{
                            put("product", product);
                            put("count", count);
                            put("expense", price * count);
                        }});
                    }
                }));
        return sortedMap(marketShare);
    }

    /**
     * 查找所有关联的产品有订单记录的经销商，销售额由高到低排序并返回。
     *
     * @return 返回一个框架为[{ dealer: Dealer, count: Long, expense: Double }, ...]的Map列表。
     */
    public List<Map<String, Object>> dealersMarketShare() {
        log.debug("Generating dealers market share.");
        Collection<Map<String, Object>> products = this.productsMarketShare();
        if (CollectionUtils.isEmpty(products)) {
            return Collections.emptyList();
        }
        Map<Long, Map<String, Object>> marketShare = new HashMap<>();
        products.forEach(item -> {
            Product product = (Product) item.get("product");
            Long count = (Long) item.get("count");
            Double expense = (Double) item.get("expense");
            Long dealerId = product.getDealer().getId();
            if (marketShare.containsKey(dealerId)) {
                Map<String, Object> dealerMap = marketShare.get(dealerId);
                dealerMap.put("count", (Long) dealerMap.get("count") + count);
                dealerMap.put("expense", (Double) dealerMap.get("expense") + expense);
            } else {
                marketShare.put(dealerId, new HashMap<String, Object>() {{
                    put("dealer", product.getDealer());
                    put("count", count);
                    put("expense", expense);
                }});
            }
        });
        return sortedMap(marketShare);
    }

    /**
     * 根据销售额由高到低排序。
     *
     * @param marketShare 销售额相关的Map列表。
     * @return 已排序的列表。
     */
    private List<Map<String, Object>> sortedMap(Map<Long, Map<String, Object>> marketShare) {
        return marketShare.values().stream()
                .sorted((p1, p2) -> {
                    double result = (Double) p2.get("expense") - (Double) p1.get("expense");
                    if (result < 0) {
                        return (int) Math.floor(result);
                    } else if (result > 0) {
                        return (int) Math.ceil(result);
                    }
                    return 0;
                })
                .collect(Collectors.toList());
    }

    /**
     * 统计订单状态。
     *
     * @return 三种订单状态与其相对应的订单个数组成的 {@link Map}对象。
     */
    public Map<String, Long> orderState() {
        Long[] stateCount = new Long[] {0L, 0L, 0L};
        List<Order> orders = orderRepository.findAll();
        if (!CollectionUtils.isEmpty(orders)) {
            orders.forEach(order -> {
                if (order.getDeliveryTime() != null) {
                    stateCount[2]++;
                } else if (order.getShipmentTime() != null) {
                    stateCount[1]++;
                } else {
                    stateCount[0]++;
                }
            });
        }
        return new HashMap<String, Long>() {{
            put("ordered", stateCount[0]);
            put("shipping", stateCount[1]);
            put("delivered", stateCount[2]);
        }};
    }
}
