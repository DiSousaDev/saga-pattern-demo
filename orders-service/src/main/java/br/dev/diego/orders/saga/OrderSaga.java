package br.dev.diego.orders.saga;

import br.dev.diego.core.dto.commands.ApproveOrderCommand;
import br.dev.diego.core.dto.commands.ProcessPaymentCommand;
import br.dev.diego.core.dto.commands.ReserveProductCommand;
import br.dev.diego.core.dto.events.OrderApprovedEvent;
import br.dev.diego.core.dto.events.OrderCreatedEvent;
import br.dev.diego.core.dto.events.PaymentProcessedEvent;
import br.dev.diego.core.dto.events.ProductReservedEvent;
import br.dev.diego.core.enums.OrderStatus;
import br.dev.diego.orders.service.OrderHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {
        "${orders.events.topic.name}",
        "${products.events.topic.name}",
        "${payments.events.topic.name}"
})
public class OrderSaga {

    @Value("${products.commands.topic.name}")
    private String productsCommandsTopicName;

    @Value("${payments.commands.topic.name}")
    private String paymentsCommandsTopicName;

    @Value("${orders.commands.topic.name}")
    private String ordersCommandsTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderHistoryService orderHistoryService;

    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate, OrderHistoryService orderHistoryService) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderHistoryService = orderHistoryService;
    }

    @KafkaHandler
    public void handleEvent(OrderCreatedEvent event) {
        ReserveProductCommand reserveProductCommand = new ReserveProductCommand(
                event.productId(),
                event.orderId(),
                event.productQuantity()
        );

        kafkaTemplate.send(productsCommandsTopicName, reserveProductCommand);
        orderHistoryService.add(event.orderId(), OrderStatus.CREATED);

    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservedEvent event) {
        ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(
                event.orderId(),
                event.productId(),
                event.productPrice(),
                event.productQuantity()
        );

        kafkaTemplate.send(paymentsCommandsTopicName, processPaymentCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentProcessedEvent event) {
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(
          event.orderId()
        );
        kafkaTemplate.send(ordersCommandsTopicName, approveOrderCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent event) {
        orderHistoryService.add(event.orderId(), OrderStatus.APPROVED);
    }

}
