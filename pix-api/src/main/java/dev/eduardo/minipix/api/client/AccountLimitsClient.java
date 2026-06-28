package dev.eduardo.minipix.api.client;

import dev.eduardo.minipix.api.dto.AccountLimitsRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
        url = "/limits/v2",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface AccountLimitsClient {

    @PostExchange("/resolve")
    Boolean assessTransactionLimit(@RequestBody AccountLimitsRequest request);

}
