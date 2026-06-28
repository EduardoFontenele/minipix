package dev.eduardo.minipix.api.service;

import dev.eduardo.minipix.api.client.DictClient;
import dev.eduardo.minipix.common.dto.PixResolveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DictService {

    private final DictClient client;

    public PixResolveResponse resolveReceiverInformation(String pixKey) {
        return client.resolveKey(pixKey);
    }
}
