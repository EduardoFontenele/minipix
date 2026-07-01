package dev.eduardo.minipix.api.facade;

import dev.eduardo.minipix.AccountType;
import dev.eduardo.minipix.PixTransaction;
import dev.eduardo.minipix.api.dto.PixInitTransactionRequest;
import dev.eduardo.minipix.api.entity.PixTransactionEntity;
import dev.eduardo.minipix.api.exception.ExternalServiceException;
import dev.eduardo.minipix.api.repository.PixTransactionRepository;
import dev.eduardo.minipix.api.security.JwtTokenParser;
import dev.eduardo.minipix.api.service.DictService;
import dev.eduardo.minipix.api.service.IdempotencyService;
import dev.eduardo.minipix.common.dto.PixResolveResponse;
import dev.eduardo.minipix.producer.PixTransactionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitTransactionFacade {
    private final JwtTokenParser jwtTokenParser;
    private final IdempotencyService idempotencyService;
    private final DictService dictService;
    private final PixTransactionProducer pixTransactionProducer;
    private final PixTransactionRepository pixTransactionRepository;

    public void execute(String authorization, String idempotencyKey, PixInitTransactionRequest input) {
        var executionStart = System.nanoTime();
        var senderDocument = jwtTokenParser.extractDocument(authorization);
        idempotencyService.checkAndStore(idempotencyKey);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var dictFuture = supplyAsync(() -> dictService.resolveReceiverInformation(input.receiverKey()), executor)
                    .orTimeout(200L, TimeUnit.MILLISECONDS);
            var start = System.nanoTime();

            PixResolveResponse dictInformation;
            try {
                dictInformation = dictFuture.join();
            } catch (CompletionException ex) {
                executor.shutdownNow();
                log.error("DICT resolution failed after {}ms — cause: {}", (System.nanoTime() - start) / 1_000_000, ex.getCause().getClass().getSimpleName());
                throw new ExternalServiceException(ex.getCause());
            }

            if (isNull(dictInformation)) throw new ExternalServiceException(new RuntimeException("DICT returned no data"));

            var transaction = PixTransaction.newBuilder()
                    .setEndToEndId(UUID.randomUUID().toString())
                    .setInitiationTimestamp(Instant.now())
                    .setAmount(input.amount())
                    .setSenderDocument(senderDocument)
                    .setReceiverKey(input.receiverKey())
                    .setReceiverName(dictInformation.receiver().name())
                    .setReceiverDocument(dictInformation.receiver().document())
                    .setReceiverIspb(dictInformation.institution().ispb())
                    .setReceiverAccountType(AccountType.valueOf(dictInformation.account().accountType().name()))
                    .setDescription(input.description())
                    .build();

            var entity = PixTransactionEntity.builder()
                    .endToEndId(transaction.getEndToEndId())
                    .initiationTimestamp(transaction.getInitiationTimestamp())
                    .amount(input.amount())
                    .senderDocument(senderDocument)
                    .receiverKey(input.receiverKey())
                    .receiverName(dictInformation.receiver().name())
                    .receiverDocument(dictInformation.receiver().document())
                    .receiverIspb(dictInformation.institution().ispb())
                    .receiverAccountType(dictInformation.account().accountType())
                    .description(input.description())
                    .build();

            pixTransactionProducer.send(transaction, executionStart)
                    .thenAcceptAsync(v -> pixTransactionRepository.save(entity));
        }
    }
}
