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
    private final AtomicReference<List<String>> feedList = new AtomicReference<>();


    // called every request, but only updates feeds when changed
    public void updateFeedListIfNecessary(List<String> newFeeds) {
        List<String> old = feedList.get();
        if (old == null || !old.equals(newFeeds)) {
            feedList.set(newFeeds);
            cachedNews.set(null);  // clear cache when feeds change
        }
    }


    /** Initialize list of feeds once */
//    public void setFeedList(List<String> feeds) {
//        this.feedList = feeds;
//    }

    /** Return cached news or fetch immediately if cache is cold */
    public Mono<JsonNode> getCachedNews() {
        JsonNode existing = cachedNews.get();
        if (existing != null) {
            return Mono.just(existing);
        }
        return refreshNow();
    }
//    public Mono<JsonNode> getCachedNews() {
//        JsonNode node = cachedNews.get();
//        if (node != null) {
//            return Mono.just(node);
//        }
//        return refreshNow();  // first time → warm-up
//    }

    /** Manually force refresh */
    public Mono<JsonNode> refreshNow(boolean force) {
        if (force) cachedNews.set(null);
        return refreshNow();
    }
    public Mono<JsonNode> refreshNow() {
        List<String> feeds = feedList.get();
        if (feeds == null || feeds.isEmpty()) {
            return Mono.error(new IllegalStateException("Feed list not set"));
        }

        System.out.println("Refreshing NOW…");

        return aggregator.aggregateFeeds(feeds)
                .map(news -> {
                    cachedNews.set(news);
                    return news;
                });
    }
//    public Mono<JsonNode> refreshNow() {
//        if (feedList == null || feedList.isEmpty()) {
//            return Mono.error(new IllegalStateException("Feed list not initialized"));
//        }
//
//        return aggregator.aggregateFeeds(feedList)
//                .doOnNext(cachedNews::set);
//    }

    /** Auto-refresh every 10 minutes */
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void refreshScheduled() {
        List<String> feeds = feedList.get();
        if (feeds == null || feeds.isEmpty()) return;

        System.out.println("[NewsCache] Auto-refreshing news...");
        aggregator.aggregateFeeds(feeds)
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
