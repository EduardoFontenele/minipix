package dev.eduardo.minipix.api.dto;

import java.math.BigDecimal;

public record PixResolveRequest(
        String receiverKey,
        BigDecimal amount
) {
}
