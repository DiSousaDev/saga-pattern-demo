package br.dev.diego.orders.service;

import br.dev.diego.core.dto.Order;
import br.dev.diego.core.dto.events.OrderApprovedEvent;
import br.dev.diego.core.dto.events.OrderCreatedEvent;
import br.dev.diego.core.enums.OrderStatus;
import br.dev.diego.orders.dao.jpa.entity.OrderEntity;
import br.dev.diego.orders.dao.jpa.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Value("${orders.events.topic.name}")
    private String ordersEventsTopicName;

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Order placeOrder(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setCustomerId(order.getCustomerId());
        entity.setProductId(order.getProductId());
        entity.setProductQuantity(order.getProductQuantity());
        entity.setStatus(OrderStatus.CREATED);
        orderRepository.save(entity);

        OrderCreatedEvent placeOrder = new OrderCreatedEvent(
                entity.getId(),
                entity.getCustomerId(),
                entity.getProductId(),
                entity.getProductQuantity()
        );

        kafkaTemplate.send(ordersEventsTopicName, placeOrder);

        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getProductId(),
                entity.getProductQuantity(),
                entity.getStatus());
    }

    @Override
    public void approveOrder(UUID uuid) {
        OrderEntity entity = orderRepository.findById(uuid).orElseThrow(
                () -> new EntityNotFoundException("Order not found: " + uuid)
        );
        entity.setStatus(OrderStatus.APPROVED);
        OrderEntity orderSaved = orderRepository.save(entity);

        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(
                orderSaved.getId()
        );
        kafkaTemplate.send(ordersEventsTopicName, orderApprovedEvent);
    }

}
