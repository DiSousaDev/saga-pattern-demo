package br.dev.diego.payments.service.handler;

import br.dev.diego.core.dto.commands.ProcessPaymentCommand;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${payments.commands.topic.name}")
public class PaymentsCommandsHandler {

    @KafkaHandler
    public void handleCommand(@Payload ProcessPaymentCommand command) {
        // Handle the command here
        // This is a placeholder for actual command handling logic
        System.out.println("Received command: " + command);
    }

}
