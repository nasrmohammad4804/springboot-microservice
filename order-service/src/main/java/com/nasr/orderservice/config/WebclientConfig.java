package com.nasr.orderservice.config;

import com.nasr.orderservice.intercept.WebclientInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebclientConfig {

    @Autowired
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private ReactiveOAuth2AuthorizedClientService oAuth2AuthorizedClientService;


    @Bean
    @LoadBalanced
//    we need to use interceptor for all request with webclient for service to service communication need to get token with internal scope
    public WebClient.Builder webClientBuilder() {

        ReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                authorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService);

        Mono<OAuth2AuthorizedClient> authorize = authorizedClientManager
                .authorize(OAuth2AuthorizeRequest.withClientRegistrationId("client-internal")
                .principal("ecommerce-client").build());

        WebClient.Builder builder = WebClient.builder();
        authorize.doOnNext(auth -> builder.filter(WebclientInterceptor.interceptor(auth.getAccessToken().getTokenValue())))
                .subscribe();

        return builder;
    }

    @Bean
    WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);


        return WebClient.builder()
                .filter(oauth2Client)
                .build();

    }
    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            final ReactiveClientRegistrationRepository clientRegistrationRepository,
            final ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        final ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
                ReactiveOAuth2AuthorizedClientProviderBuilder
                        .builder()
                        .clientCredentials()
                        .build();
        final AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }
}
