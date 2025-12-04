package com.pawamax.news.controllers;


import com.pawamax.news.services.NewsAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;
import com.pawamax.news.services.FeedAggregatorService;
import com.pawamax.news.services.NewsAggregatorService;
import com.pawamax.news.services.RedditFeedService;
import com.pawamax.news.services.TwitterFeedService;
import com.pawamax.news.services.YouTubeFeedService;
import com.pawamax.news.services.GeneralRssService;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.JsonNode;
import com.pawamax.news.services.CacheService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final NewsAggregatorService newsAggregatorService;
    private final CacheService cacheService;
    private final FeedAggregatorService aggregator;
    private final TwitterFeedService twitterService;
    private final RedditFeedService redditService;
    private final YouTubeFeedService youtubeService;
    private final GeneralRssService generalRssService;


    @GetMapping("/all")
    public Mono<JsonNode> all(
            @RequestParam(name = "feeds") String feedsCsv,
            @RequestParam(name = "refresh", defaultValue = "false") boolean refresh
    ) {

        List<String> feeds = Arrays.stream(feedsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // initialize feeds list in cache
        cacheService.updateFeedListIfNecessary(feeds);

        if (refresh) {
            System.out.println("Manual refresh triggered...");
            return cacheService.refreshNow(true);
        }

        System.out.println("Returning cached news...");
        return cacheService.getCachedNews();
    }



//    @GetMapping("/all")
//    public Mono<JsonNode> all(@RequestParam(name = "feeds") String feedsCsv) {
//        List<String> feeds = Arrays.stream(feedsCsv.split(","))
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .collect(Collectors.toList());
//        return aggregator.aggregateFeeds(feeds);
//    }

//    @GetMapping
//    public Mono<String> getNews() {
//        return newsAggregatorService.getAllNews();
//    }
//
//
//    @GetMapping("/twitter/{username}")
//    public Mono<JsonNode> twitter(@PathVariable String username) {
//        return twitterService.getUserTweets(username);
//    }
//
//    @GetMapping("/reddit/{sub}")
//    public Mono<JsonNode> reddit(@PathVariable String sub) {
//        return redditService.getSubredditFeed(sub);
//    }
//
//    @GetMapping("/youtube/{channelId}")
//    public Mono<JsonNode> youtube(@PathVariable String channelId) {
//        return youtubeService.getChannelFeed(channelId);
//    }
//
//    @GetMapping("/rss")
//    public Mono<JsonNode> rss(@RequestParam String url) {
//        return generalRssService.getFeed(url);
//    }
}
