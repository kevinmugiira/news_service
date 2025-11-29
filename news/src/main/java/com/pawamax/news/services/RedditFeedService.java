package com.pawamax.news.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class RedditFeedService {

    private final FeedService feedService;

    public Mono<JsonNode> getSubredditFeed(String subreddit) {
        String rss = "https://www.reddit.com/r/" + subreddit + "/new.rss";
        return feedService.fetchFeed(rss);
    }
}
