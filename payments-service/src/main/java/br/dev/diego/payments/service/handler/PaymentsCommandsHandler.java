package br.dev.diego.payments.service.handler;

import br.dev.diego.core.dto.Payment;
import br.dev.diego.core.dto.commands.ProcessPaymentCommand;
import br.dev.diego.core.dto.events.PaymentFailedEvent;
import br.dev.diego.core.dto.events.PaymentProcessedEvent;
import br.dev.diego.core.exceptions.CreditCardProcessorUnavailableException;
import br.dev.diego.payments.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${payments.commands.topic.name}")
public class PaymentsCommandsHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${payments.events.topic.name}")
    private String paymentsEventsTopic;

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentsCommandsHandler(PaymentService paymentService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaHandler
    public void handleCommand(@Payload ProcessPaymentCommand command) {

        try {
            Payment payment = new Payment(
                    command.orderId(),
                    command.productId(),
                    command.productPrice(),
                    command.productQuantity()
            );

            Payment processedPayment = paymentService.process(payment);

            PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
                    processedPayment.getOrderId(),
                    processedPayment.getId()
            );

            kafkaTemplate.send(paymentsEventsTopic, paymentProcessedEvent);

        } catch (CreditCardProcessorUnavailableException e) {
            log.error(e.getLocalizedMessage(), e);
            PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                    command.orderId(),
                    command.productId(),
                    command.productQuantity()
            );
            kafkaTemplate.send(paymentsEventsTopic, paymentFailedEvent);
        }
    }

}
