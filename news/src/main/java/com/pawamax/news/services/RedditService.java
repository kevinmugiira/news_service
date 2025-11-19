package com.pawamax.news.services;

//import lombok.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RedditService {

    @Value("${reddit.base-url}")
    private String redditBaseUrl;

    private final WebClient webClient;

    public RedditService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public Mono<String> getTopPosts(String subreddit) {
        return webClient.get()
                .uri(redditBaseUrl + "/r/" + subreddit + "/top.json?Limit=10")
                .retrieve()
                .bodyToMono(String.class);
    }
}
