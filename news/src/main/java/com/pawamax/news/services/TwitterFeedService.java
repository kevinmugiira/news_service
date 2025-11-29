package com.pawamax.news.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TwitterFeedService {

    private final FeedService feedService;

    public Mono<String> getUserTweets(String username) {
        String rss = "https://rsshub.app/twitter/user/" + username;
        return feedService.fetchFeed(rss);
    }
}
