package dev.eduardo.minipix.api.resource;

import dev.eduardo.minipix.api.dto.PixInitTransactionRequest;
import dev.eduardo.minipix.api.dto.ResolveReceiverInputWrapper;
import dev.eduardo.minipix.api.dto.PixResolveRequest;
import dev.eduardo.minipix.api.facade.InitTransactionFacade;
import dev.eduardo.minipix.api.facade.ResolveReceiverFacade;
import dev.eduardo.minipix.common.dto.PixResolveResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static dev.eduardo.minipix.api.util.PixHeaders.AUTHORIZATION;
import static dev.eduardo.minipix.api.util.PixHeaders.CORRELATION_ID;
import static dev.eduardo.minipix.api.util.PixHeaders.DEVICE_ID;
import static dev.eduardo.minipix.api.util.PixHeaders.FORWARDED_FOR;
import static dev.eduardo.minipix.api.util.PixHeaders.IDEMPOTENCY_KEY;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixResource {

    private final ResolveReceiverFacade resolveReceiverFacade;
    private final InitTransactionFacade initTransactionFacade;

    @PostMapping("/transactions/resolve")
    public ResponseEntity<PixResolveResponse> resolveReceiverInformations(
            @Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String authorization,
            @RequestHeader(CORRELATION_ID) String correlationId,
            @RequestHeader(IDEMPOTENCY_KEY) String idempotencyKey,
            @RequestHeader(DEVICE_ID) String deviceId,
            @RequestHeader(FORWARDED_FOR) String forwaredFor,
            @RequestBody PixResolveRequest request
    ) {
        var input = new ResolveReceiverInputWrapper(authorization, correlationId, idempotencyKey, deviceId, forwaredFor, request);
        var result = resolveReceiverFacade.execute(input);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/transactions/init")
    public ResponseEntity<Void> initTransaction(
            @Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String authorization,
            @RequestHeader(IDEMPOTENCY_KEY) String idempotencyKey,
            @RequestBody PixInitTransactionRequest request
    ) {
        initTransactionFacade.execute(authorization, idempotencyKey, request);
        return ResponseEntity.accepted().build();
    }
}
