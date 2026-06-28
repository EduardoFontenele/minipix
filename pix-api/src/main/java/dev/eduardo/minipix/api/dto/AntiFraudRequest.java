package dev.eduardo.minipix.api.dto;

public record AntiFraudRequest(
        String receiverKey,
        String senderDocument,
        String deviceId,
        String forwardedFor
) {}
