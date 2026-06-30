package dev.eduardo.minipix.api.dto;

public record ResolveReceiverInputWrapper(
        String authorization,
        String correlationId,
        String idempotencyKey,
        String deviceId,
        String forwardedFor,
        PixResolveRequest request
) {}
