package com.pawamax.news.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class FeedAggregatorService {

    private final FeedService feedService; // returns Mono<JsonNode> from rss2json

    /**
     * Fetch multiple RSS feed endpoints (rss2json-wrapped URLs or RSSHub URLs),
     * extract their "items" arrays and combine into a single ArrayNode sorted by pubDate desc.
     *
     * @param feedRssUrls list of rss (or rsshub) URLs to convert via rss2json
     * @return Mono<JsonNode> that is an ObjectNode: { "items": [ ... ] }
     */
    public Mono<JsonNode> aggregateFeeds(List<String> feedUrls) {
        List<Mono<JsonNode>> monos = feedUrls.stream()
                .map(feedService::fetchFeed)
                .toList();

        return Flux.mergeSequential(monos)
                .flatMapIterable(json -> {
                    if (json == null) return List.<JsonNode>of();

                    // RSS2JSON structure: "items" array
                    JsonNode items = json.get("items");
                    if (items != null && items.isArray()) return items;

                    // RSSHub JSON: usually top-level array
                    if (json.isArray()) return json;

                    return List.<JsonNode>of();
                })
                .collectList()
                .map(list -> {
                    var mapper = com.fasterxml.jackson.databind.json.JsonMapper.builder().build();
                    ArrayNode combined = mapper.createArrayNode();
                    list.forEach(combined::add);

                    // Sort by pubDate (newest first)
                    var sorted = StreamSupport.stream(combined.spliterator(), false)
                            .sorted(Comparator.comparing((JsonNode n) -> {
                                var dateNode = n.get("pubDate");
                                if (dateNode != null && dateNode.isTextual()) {
                                    try {
                                        return Instant.parse(dateNode.asText());
                                    } catch (DateTimeParseException e) {
                                        return Instant.EPOCH;
                                    }
                                }
                                return Instant.EPOCH;
                            }).reversed())
                            .toList();

                    ArrayNode out = mapper.createArrayNode();
                    sorted.forEach(out::add);

                    ObjectNode root = mapper.createObjectNode();
                    root.set("items", out);
                    root.put("count", out.size());
                    return (JsonNode) root;
                });
    }







//    public Mono<JsonNode> aggregateFeeds(List<String> feedRssUrls) {
//        // fetch each feed as JsonNode using feedService.fetchFeed(rssUrl)
//        List<Mono<JsonNode>> monos = feedRssUrls.stream()
//                .map(feedService::fetchFeed) // each returns Mono<JsonNode> from rss2json.com
//                .toList();
//
//        // Combine all Monos into a Flux of JsonNode
//        return Flux.mergeSequential(monos)
//                .flatMapIterable(json -> {
//                    // rss2json returns "items" array; fallback if not present
//                    JsonNode items = json.get("items");
//                    if (items != null && items.isArray()) {
//                        return items;
//                    }
//                    // try common RSSHub structure: items may be top-level array
//                    if (json.isArray()) {
//                        return json;
//                    }
//                    return List.<JsonNode>of();
//                })
//                .collectList()
//                .map(list -> {
//                    // Create combined ArrayNode
//                    var mapper = com.fasterxml.jackson.databind.json.JsonMapper.builder().build();
//                    ArrayNode combined = mapper.createArrayNode();
//                    list.forEach(combined::add);
//
//                    // Sort combined by pubDate (if present), newest first
//                    var sorted = StreamSupport.stream(combined.spliterator(), false)
//                            .sorted(Comparator.comparing((JsonNode n) -> {
//                                        var dateNode = n.get("pubDate");
//                                        if (dateNode != null && dateNode.isTextual()) {
//                                            try {
//                                                return Instant.parse(dateNode.asText());
//                                            } catch (DateTimeParseException e) {
//                                                // try RFC822 style fallback
//                                                try {
//                                                    return Instant.parse(dateNode.asText());
//                                                } catch (Exception ex) {
//                                                    return Instant.EPOCH;
//                                                }
//                                            }
//                                        }
//                                        return Instant.EPOCH;
//                                    }).reversed()
//                            )
//                            .toList();
//
//                    // put sorted back into ArrayNode
//                    ArrayNode out = mapper.createArrayNode();
//                    sorted.forEach(out::add);
//
//                    ObjectNode root = mapper.createObjectNode();
//                    root.set("items", out);
//                    root.put("count", out.size());
//                    return (JsonNode) root;
//                });
//    }
}
