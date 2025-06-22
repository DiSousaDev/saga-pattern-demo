package br.dev.diego.products.service.handler;

import br.dev.diego.core.dto.Product;
import br.dev.diego.core.dto.commands.ReserveProductCommand;
import br.dev.diego.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${products.commands.topic.name}"})
public class ProductsCommandsHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ProductService productService;

    public ProductsCommandsHandler(ProductService productService) {
        this.productService = productService;
    }

    @KafkaHandler
    public void handleCommand(@Payload ReserveProductCommand command) {

        try {
            Product desiredProduct = new Product(command.productId(), command.productQuantity());
            Product reservedProduct = productService.reserve(desiredProduct, command.orderId());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }

    }

}
