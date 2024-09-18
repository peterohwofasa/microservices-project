package com.petermicroservice.inventoryservice.controllers;

import com.petermicroservice.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{sku-code}")
    @ResponseStatus(HttpStatus.OK)
    public boolean inInstock(@PathVariable("sku-code") String skuCode) {

        return inventoryService.isInStock(skuCode);
    }

    @PostMapping("/check-stock")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> areItemsInStock(@RequestBody List<String> skuCodes) {
        return skuCodes.stream()
                .filter(sku -> sku != null && !sku.isEmpty())  // Filter out null or empty SKUs
                .collect(Collectors.toMap(
                        sku -> sku,
                        sku -> inventoryService.isInStock(sku),
                        (existing, replacement) -> existing  // Handle duplicate SKU codes
                ));
    }

}
