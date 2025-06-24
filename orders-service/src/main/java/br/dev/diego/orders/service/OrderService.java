package br.dev.diego.orders.service;

import br.dev.diego.core.dto.Order;

import java.util.UUID;

public interface OrderService {
    Order placeOrder(Order order);

    void approveOrder(UUID uuid);
}
