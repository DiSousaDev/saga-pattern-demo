package br.dev.diego.core.dto.commands;

import java.util.UUID;

public record ReserveProductCommand(
        UUID productId,
        UUID orderId,
        Integer productQuantity
) {
}
