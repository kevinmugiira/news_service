package com.pawamax.news.services;

//import lombok.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NitterService {

    @Value("${nitter.base-url}")
    private String nitterHost;

    private final WebClient webClient;

    public NitterService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public Mono<String> getUserTweets(String username) {
        return webClient.get()
                .uri(nitterHost + "/" + username + "/rss")
                .retrieve()
                .bodyToMono(String.class);
    }
}
