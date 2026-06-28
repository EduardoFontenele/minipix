package dev.eduardo.minipix.common.dto;

import dev.eduardo.minipix.common.enums.PixKeyType;

import java.time.LocalDateTime;

public record PixResolveResponse(
        ReceiverInfo receiver,
        AccountInfo account,
        InstitutionInfo institution,
        String key,
        PixKeyType keyType,
        LocalDateTime keyRegisteredAt
) {}
