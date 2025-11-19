package com.pawamax.news.services;


import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class NewsAggregatorService {

    private final RedditService redditService;
    private final NitterService nitterService;

    public NewsAggregatorService(RedditService redditService, NitterService nitterService) {
        this.redditService = redditService;
        this.nitterService = nitterService;
    }

    public Mono<String> getAllNews() {
        return Mono.zip(
                redditService.getTopPosts("worldnews"),
                nitterService.getUserTweets("BBCWorld")
        ).map(tuple -> "{ \"redditService\", " + tuple.getT1() + ", \"twitter\": \"" + tuple.getT2() + "\"}");

    }
}
