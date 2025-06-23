package br.dev.diego.core.dto.commands;

import java.util.UUID;

public record ApproveOrderCommand(
        UUID orderId
) {
}
