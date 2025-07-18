package br.dev.diego.core.dto.events;

import java.util.UUID;

public record PaymentProcessedEvent(
        UUID orderId,
        UUID paymentId
) {
}
