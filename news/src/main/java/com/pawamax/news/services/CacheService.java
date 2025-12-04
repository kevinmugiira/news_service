package com.pawamax.news.services;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final FeedAggregatorService aggregator;
    private final AtomicReference<JsonNode> cachedNews = new AtomicReference<>();
    private List<String> feedList;

    /** Initialize list of feeds once */
    public void setFeedList(List<String> feeds) {
        this.feedList = feeds;
    }

    /** Return cached news or fetch immediately if cache is cold */
    public Mono<JsonNode> getCachedNews() {
        JsonNode node = cachedNews.get();
        if (node != null) {
            return Mono.just(node);
        }
        return refreshNow();  // first time → warm-up
    }

    /** Manually force refresh */
    public Mono<JsonNode> refreshNow() {
        if (feedList == null || feedList.isEmpty()) {
            return Mono.error(new IllegalStateException("Feed list not initialized"));
        }

        return aggregator.aggregateFeeds(feedList)
                .doOnNext(cachedNews::set);
    }

    /** Auto-refresh every 10 minutes */
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void refreshScheduled() {
        if (feedList == null || feedList.isEmpty()) return;

        System.out.println("[NewsCache] Auto-refreshing news...");
        aggregator.aggregateFeeds(feedList)
                .doOnNext(cachedNews::set)
                .subscribe(
                        ok -> System.out.println("[NewsCache] Auto-refresh complete."),
                        err -> System.err.println("[NewsCache] Error refreshing: " + err)
                );
    }
}




//@Service
//public class NewsCacheService {
//
//    private List<NewsItem> cachedNews = new ArrayList<>();
//    private final FeedAggregatorService feedAggregatorService; // NewsFetcherService newsFetcherService;
//
//    public NewsCacheService(FeedAggregatorService newsAggregatorService) {
//        this.feedAggregatorService = feedAggregatorService;
//    }
//
//    public List<NewsItem> getCachedNews() {
//        return cachedNews;
//    }
//
//    // Refresh every 10 minutes (customize if you want)
//    @Scheduled(fixedRate = 10 * 60 * 1000)
//    public void refreshNews() {
//        try {
//            System.out.println("Refreshing news feeds...");
//            cachedNews = feedAggregatorService.aggregateFeeds();
//        } catch (Exception e) {
//            System.err.println("Failed to refresh news: " + e.getMessage());
//        }
//    }
//}
