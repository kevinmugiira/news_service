package com.pawamax.news.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class YouTubeFeedService {

    private final FeedService feedService;

    public Mono<String> getChannelFeed(String channelId) {
        String rss = "https://www.youtube.com/feeds/videos.xml?channel_id=" + channelId;
        return feedService.fetchFeed(rss);
    }
}
