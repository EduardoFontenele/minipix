package dev.eduardo.minipix.api.dto;

public record PixResolveInput(
        String authorization,
        String correlationId,
        String idempotencyKey,
        String deviceId,
        String forwardedFor,
        PixResolveRequest request
) {}
