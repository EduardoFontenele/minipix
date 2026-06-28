package dev.eduardo.minipix.api.service;

import dev.eduardo.minipix.api.client.AntiFraudClient;
import dev.eduardo.minipix.api.dto.AntiFraudRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AntiFraudService {
    private final AntiFraudClient antiFraudClient;

    public Boolean assessFraudRisk(String receiverKey, String senderDocument, String deviceId, String forwardedFor) {
        var request = new AntiFraudRequest(receiverKey, senderDocument, deviceId, forwardedFor);
        return antiFraudClient.assessFraudRisk(request);
    }
}
