package com.pawamax.news.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class GeneralRssService {

    private final FeedService feedService;

    public Mono<JsonNode> getFeed(String rssUrl) {
        return feedService.fetchFeed(rssUrl);
    }
}
