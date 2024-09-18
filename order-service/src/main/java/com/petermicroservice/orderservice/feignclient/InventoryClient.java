package com.petermicroservice.orderservice.feignclient;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "inventory-service", url = "http://localhost:8082")
public interface InventoryClient {

    @Cacheable("inventory")
    @PostMapping("/api/inventory/check-stock")
    Map<String, Boolean> areItemsInStock(@RequestBody List<String> skuCodes);
}
