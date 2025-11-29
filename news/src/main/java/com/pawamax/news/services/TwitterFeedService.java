package com.pawamax.news.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class TwitterFeedService {

    private final FeedService feedService;

    public Mono<JsonNode> getUserTweets(String username) {
        String rss = "https://rsshub.app/twitter/user/" + username;
        return feedService.fetchFeed(rss);
    }
}
