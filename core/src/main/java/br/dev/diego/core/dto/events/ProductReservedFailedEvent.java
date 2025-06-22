package br.dev.diego.core.dto.events;

import java.util.UUID;

public record ProductReservedFailedEvent(
        UUID orderId,
        UUID productId,
        Integer productQuantity
) {
}
