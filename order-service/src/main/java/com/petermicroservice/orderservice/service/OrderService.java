package com.petermicroservice.orderservice.service;

import com.petermicroservice.orderservice.dto.OrderLineItemsDto;
import com.petermicroservice.orderservice.dto.OrderRequest;
import com.petermicroservice.orderservice.feignclient.InventoryClient;
import com.petermicroservice.orderservice.model.Order;
import com.petermicroservice.orderservice.model.OrderLineItems;
import com.petermicroservice.orderservice.repository.OrderRepository;
import com.petermicroservice.orderservice.exception.OutOfStockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        // Collect skuCodes for bulk check
        List<String> skuCodes = orderLineItems.stream()
                .map(OrderLineItems::getSkuScode)
                .collect(Collectors.toList());

        // Perform bulk inventory check
        Map<String, Boolean> stockStatus = inventoryClient.areItemsInStock(skuCodes);

        // Check if all items are in stock
        boolean allInStock = stockStatus.values().stream().allMatch(Boolean::booleanValue);

        if (allInStock) {
            order.setOrderLineItemsList(orderLineItems);
            orderRepository.save(order);
        } else {
            throw new OutOfStockException("One or more items are not in stock.");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuatity());
        orderLineItems.setSkuScode(orderLineItemsDto.getSkuScode());
        return orderLineItems;
    }
}
