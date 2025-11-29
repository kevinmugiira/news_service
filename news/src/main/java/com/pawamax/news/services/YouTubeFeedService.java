package com.pawamax.news.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class YouTubeFeedService {

    private final FeedService feedService;

    public Mono<JsonNode> getChannelFeed(String channelId) {
        String rss = "https://www.youtube.com/feeds/videos.xml?channel_id=" + channelId;
        return feedService.fetchFeed(rss);
    }
}
