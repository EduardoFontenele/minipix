package dev.eduardo.minipix.api.facade;

import dev.eduardo.minipix.api.dto.PixInitTransactionRequest;
import dev.eduardo.minipix.api.security.JwtTokenParser;
import dev.eduardo.minipix.api.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitTransactionFacade {
    private final JwtTokenParser jwtTokenParser;
    private final IdempotencyService idempotencyService;

    public void execute(String authorization, String idempotencyKey, PixInitTransactionRequest input) {
        var senderDocument = jwtTokenParser.extractDocument(authorization);
        idempotencyService.checkAndStore(idempotencyKey);

    }
}
