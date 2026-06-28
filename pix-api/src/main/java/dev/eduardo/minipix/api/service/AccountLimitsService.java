package dev.eduardo.minipix.api.service;

import dev.eduardo.minipix.api.client.AccountLimitsClient;
import dev.eduardo.minipix.api.dto.AccountLimitsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountLimitsService {

    private final AccountLimitsClient client;

    public Boolean assessTransactionLimit(String senderDocument, BigDecimal amount) {
        var request = new AccountLimitsRequest(senderDocument, amount);
        return client.assessTransactionLimit(request);
    }
}
