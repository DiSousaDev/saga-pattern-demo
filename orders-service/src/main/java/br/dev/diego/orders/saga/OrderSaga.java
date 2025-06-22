package br.dev.diego.orders.saga;

import br.dev.diego.core.dto.commands.ReserveProductCommand;
import br.dev.diego.core.dto.events.OrderCreatedEvent;
import br.dev.diego.core.enums.OrderStatus;
import br.dev.diego.orders.service.OrderHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${orders.events.topic.name}"})
public class OrderSaga {

    @Value("${products.commands.topic.name}")
    private String productsCommandsTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderHistoryService orderHistoryService;

    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate, OrderHistoryService orderHistoryService) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderHistoryService = orderHistoryService;
    }

    @KafkaHandler
    public void handleEvent(OrderCreatedEvent event) {
        ReserveProductCommand reserveProductCommand = new ReserveProductCommand(
                event.orderId(),
                event.productId(),
                event.productQuantity()
        );

        kafkaTemplate.send(productsCommandsTopicName, reserveProductCommand);
        orderHistoryService.add(event.orderId(), OrderStatus.CREATED);

    }

}
