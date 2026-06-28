package dev.eduardo.minipix.api.facade;

import dev.eduardo.minipix.api.dto.PixResolveInput;
import dev.eduardo.minipix.api.exception.ExternalServiceException;
import dev.eduardo.minipix.api.exception.TransactionNotAllowedException;
import dev.eduardo.minipix.api.service.AccountLimitsService;
import dev.eduardo.minipix.api.service.AntiFraudService;
import dev.eduardo.minipix.api.service.DictService;
import dev.eduardo.minipix.api.util.DocumentUtils;
import dev.eduardo.minipix.common.dto.PixResolveResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReceiverResolutionFacade {

    @Value("${pix.security.jwt-secret}")
    private String jwtSecret;

    private final AccountLimitsService accountLimitsService;
    private final AntiFraudService antiFraudService;
    private final DictService dictService;

    public PixResolveResponse getReceiverInformation(PixResolveInput input) {
        var senderDocument = extractDocument(input.authorization());

        try(var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var sufficientLimitFuture = supplyAsync(
                    () -> accountLimitsService.assessTransactionLimit(senderDocument, input.request().amount()), executor)
                    .orTimeout(200L, MILLISECONDS);

            var fraudRiskFuture = supplyAsync(() -> antiFraudService.assessFraudRisk(
                    input.request().receiverKey(),
                    senderDocument,
                    input.deviceId(),
                    input.forwardedFor()), executor)
                    .orTimeout(200L, MILLISECONDS);

            var dictInfoFuture = supplyAsync(() -> dictService.resolveReceiverInformation(input.request().receiverKey()), executor)
                    .orTimeout(200L, MILLISECONDS);

            var start = System.nanoTime();
            try {
                allOf(sufficientLimitFuture, fraudRiskFuture, dictInfoFuture).join();
            } catch (CompletionException ex) {
                executor.shutdownNow();
                log.error("Parallel resolution failed after {}ms — cause: {}", (System.nanoTime() - start) / 1_000_000, ex.getCause().getClass().getSimpleName());
                throw new ExternalServiceException(ex.getCause());
            }
            var hasSufficientLimit = sufficientLimitFuture.join();
            var hasFraudRisk = fraudRiskFuture.join();

            log.info("resolve correlationId={} receiverKey={} senderDocument={} hasFraudRisk={} hasSufficientLimits={} elapsedMs={}",
                    input.correlationId(),
                    input.request().receiverKey(),
                    DocumentUtils.maskCpf(senderDocument),
                    hasFraudRisk,
                    hasSufficientLimit,
                    (System.nanoTime() - start) / 1_000_000);

            if (hasFraudRisk) throw new TransactionNotAllowedException("Transaction blocked due to fraud risk");
            if (!hasSufficientLimit) throw new TransactionNotAllowedException("Sender has insufficient transaction limit");

            var dictInformation = dictInfoFuture.join();
            if (isNull(dictInformation)) throw new ExternalServiceException(new RuntimeException("DICT returned no data"));
            return dictInformation;
        }
    }

    private String extractDocument(String authorizationHeader) {
        var token = authorizationHeader.replace("Bearer ", "");
        var key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
