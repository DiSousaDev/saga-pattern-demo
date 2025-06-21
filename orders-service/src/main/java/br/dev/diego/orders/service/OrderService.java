package br.dev.diego.orders.service;

import br.dev.diego.core.dto.Order;

public interface OrderService {
    Order placeOrder(Order order);
}
