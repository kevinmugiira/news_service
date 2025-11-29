package com.pawamax.news.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class FeedService {

    @Autowired
    private WebClient webClient;


    /**
     * Fetch a feed URL as JsonNode.
     * If the URL is RSSHub, fetch directly.
     * Otherwise, use rss2json.com as a converter.
     */
    public Mono<JsonNode> fetchFeed(String rssUrl) {
        String uri;

        if (rssUrl.contains("rsshub.app")) {
            // RSSHub JSON endpoint
            uri = rssUrl;
        } else {
            // Use rss2json.com as fallback for standard RSS
            uri = "https://api.rss2json.com/v1/api.json?rss_url=" + rssUrl;
        }

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorResume(e -> {
                    // Catch errors and return empty items
                    System.err.println("Failed to fetch feed: " + rssUrl + " | " + e.getMessage());
                    return Mono.just(webClient.get().uri(uri).retrieve().bodyToMono(JsonNode.class)
                            .blockOptional().orElseGet(() -> null));
                });
    }
//}


//    public Mono<JsonNode> fetchFeed(String rssUrl) {
//        if (rssUrl.contains("rsshub.app")) {
//            return webClient.get()
//                    .uri(rssUrl)
//                    .retrieve()
//                    .bodyToMono(JsonNode.class);
//        } else {
//            String converter = "https://api.rss2json.com/v1/api.json?rss_url=";
//            return webClient.get()
//                    .uri(converter + rssUrl)
//                    .retrieve()
//                    .bodyToMono(JsonNode.class);
//        }
//    }


//    public Mono<JsonNode> fetchFeed(String rssUrl) {
//        String converter = "https://api.rss2json.com/v1/api.json?rss_url=";
//
//        return webClient.get()
//                .uri(converter + rssUrl)
//                .retrieve()
//                .bodyToMono(JsonNode.class);
//    }
}
