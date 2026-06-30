package dev.eduardo.minipix.api.dto;

import java.math.BigDecimal;

public record PixInitTransactionRequest(
        BigDecimal amount,
        String receiverKey,
        String description
) {
}
