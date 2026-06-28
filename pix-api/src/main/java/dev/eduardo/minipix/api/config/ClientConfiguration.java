package dev.eduardo.minipix.api.config;

import dev.eduardo.minipix.api.client.AccountLimitsClient;
import dev.eduardo.minipix.api.client.AntiFraudClient;
import dev.eduardo.minipix.api.client.DictClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;

@Configuration
public class ClientConfiguration {

    private final String baseUrl = "http://localhost:9090";

    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory() {
        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
    }

    @Bean
    public DictClient dictClient(HttpServiceProxyFactory factory) {
        return factory.createClient(DictClient.class);
    }

    @Bean
    public AntiFraudClient antiFraudClient(HttpServiceProxyFactory factory) {
        return factory.createClient(AntiFraudClient.class);
    }

    @Bean
    public AccountLimitsClient accountLimitsClient(HttpServiceProxyFactory factory) {
        return factory.createClient(AccountLimitsClient.class);
    }
}
