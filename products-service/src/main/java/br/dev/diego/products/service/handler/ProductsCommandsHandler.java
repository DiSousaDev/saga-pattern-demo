package br.dev.diego.products.service.handler;

import br.dev.diego.core.dto.Product;
import br.dev.diego.core.dto.commands.ReserveProductCommand;
import br.dev.diego.core.dto.events.ProductReservedEvent;
import br.dev.diego.core.dto.events.ProductReservedFailedEvent;
import br.dev.diego.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${products.commands.topic.name}"})
public class ProductsCommandsHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${products.events.topic.name}")
    private String productsEventsTopicName;

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductsCommandsHandler(ProductService productService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.productService = productService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaHandler
    public void handleCommand(@Payload ReserveProductCommand command) {

        try {
            Product desiredProduct = new Product(command.productId(), command.productQuantity());
            Product reservedProduct = productService.reserve(desiredProduct, command.orderId());

            ProductReservedEvent productReservedEvent = new ProductReservedEvent(
                    command.orderId(),
                    command.productId(),
                    reservedProduct.getPrice(),
                    command.productQuantity()
            );

            kafkaTemplate.send(productsEventsTopicName, productReservedEvent);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            ProductReservedFailedEvent productReservedFailedEvent = new ProductReservedFailedEvent(
                    command.orderId(),
                    command.productId(),
                    command.productQuantity()
            );

            kafkaTemplate.send(productsEventsTopicName, productReservedFailedEvent);
        }

    }

}
