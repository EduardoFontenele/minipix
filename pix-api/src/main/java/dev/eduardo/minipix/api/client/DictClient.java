package dev.eduardo.minipix.api.client;

import dev.eduardo.minipix.common.dto.PixResolveResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(
        url = "/dict/v2",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface DictClient {

    @GetExchange(
            url = "/key/{key}",
            accept = MediaType.APPLICATION_JSON_VALUE
    )
    PixResolveResponse resolveKey(@PathVariable String key);

}
