package br.dev.diego.orders.service;

import br.dev.diego.core.enums.OrderStatus;
import br.dev.diego.orders.dto.OrderHistory;

import java.util.List;
import java.util.UUID;

public interface OrderHistoryService {
    void add(UUID orderId, OrderStatus orderStatus);

    List<OrderHistory> findByOrderId(UUID orderId);
}
