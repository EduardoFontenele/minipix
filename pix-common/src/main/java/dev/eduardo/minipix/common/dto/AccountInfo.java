package dev.eduardo.minipix.common.dto;

import dev.eduardo.minipix.common.enums.AccountType;

public record AccountInfo(
        String maskedBranch,
        String maskedAccountNumber,
        AccountType accountType
) {}
