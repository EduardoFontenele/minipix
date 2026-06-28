package dev.eduardo.minipix.common.dto;

import dev.eduardo.minipix.common.enums.PersonType;

public record ReceiverInfo(
        String name,
        PersonType personType,
        String maskedDocument,
        String document
) {}
