package br.dev.diego.orders.saga;

import br.dev.diego.core.dto.events.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${orders.events.topic.name}"})
public class OrderSaga {

    @KafkaHandler
    public void handleEvent(OrderCreatedEvent event) {}

}
