package br.dev.diego.orders.service;

import br.dev.diego.core.enums.OrderStatus;
import br.dev.diego.orders.dao.jpa.entity.OrderHistoryEntity;
import br.dev.diego.orders.dao.jpa.repository.OrderHistoryRepository;
import br.dev.diego.orders.dto.OrderHistory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;

    public OrderHistoryServiceImpl(OrderHistoryRepository orderHistoryRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @Override
    public void add(UUID orderId, OrderStatus orderStatus) {
        OrderHistoryEntity entity = new OrderHistoryEntity();
        entity.setOrderId(orderId);
        entity.setStatus(orderStatus);
        entity.setCreatedAt(new Timestamp(new Date().getTime()));
        orderHistoryRepository.save(entity);
    }

    @Override
    public List<OrderHistory> findByOrderId(UUID orderId) {
        var entities = orderHistoryRepository.findByOrderId(orderId);
        return entities.stream().map(entity -> {
            OrderHistory orderHistory = new OrderHistory();
            BeanUtils.copyProperties(entity, orderHistory);
            return orderHistory;
        }).toList();
    }
}
