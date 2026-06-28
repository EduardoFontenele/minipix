package dev.eduardo.minipix.api.dto;

import java.math.BigDecimal;

public record AccountLimitsRequest(
        String senderDocument,
        BigDecimal amount
) {}
