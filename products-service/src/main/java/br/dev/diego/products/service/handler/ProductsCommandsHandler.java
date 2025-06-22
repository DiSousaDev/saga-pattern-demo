package br.dev.diego.products.service.handler;

import br.dev.diego.core.dto.commands.ReserveProductCommand;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${products.commands.topic.name}"})
public class ProductsCommandsHandler {

    @KafkaHandler
    public void handleCommand(@Payload ReserveProductCommand command) {
        // Handle the command here
        // This is a placeholder for actual command handling logic
        System.out.println("Received command: " + command);
    }

}
