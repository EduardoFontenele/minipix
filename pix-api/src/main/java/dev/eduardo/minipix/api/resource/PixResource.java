package dev.eduardo.minipix.api.resource;

import dev.eduardo.minipix.api.dto.PixResolveInput;
import dev.eduardo.minipix.api.dto.PixResolveRequest;
import dev.eduardo.minipix.api.facade.ReceiverResolutionFacade;
import dev.eduardo.minipix.api.util.Descriptions;
import dev.eduardo.minipix.common.dto.PixResolveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    private final ReceiverResolutionFacade receiverResolutionFacade;

    @Operation(description = Descriptions.RESOLVE)
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/transactions/resolve")
    public ResponseEntity<PixResolveResponse> resolveReceiverInformations(
            @RequestHeader(AUTHORIZATION) String authorization,
            @RequestHeader(CORRELATION_ID) String correlationId,
            @RequestHeader(IDEMPOTENCY_KEY) String idempotencyKey,
            @RequestHeader(DEVICE_ID) String deviceId,
            @RequestHeader(FORWARDED_FOR) String forwaredFor,
            @RequestBody PixResolveRequest request
    ) {
        var input = new PixResolveInput(authorization, correlationId, idempotencyKey, deviceId, forwaredFor, request);
        var result = receiverResolutionFacade.getReceiverInformation(input);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/transactions/init")
    public ResponseEntity<Void> initTransaction() {
        return ResponseEntity.accepted().build();
    }
}
