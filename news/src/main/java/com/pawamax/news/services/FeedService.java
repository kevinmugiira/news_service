package com.pawamax.news.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final WebClient webClient;

    public Mono<String> fetchFeed(String rssUrl) {
        String converter = "https://api.rss2json.com/v1/api.json?rss_url=";

        return webClient.get()
                .uri(converter + rssUrl)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
}
