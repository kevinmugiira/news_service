package com.pawamax.news.controllers;


import com.pawamax.news.services.NewsAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsAggregatorService newsAggregatorService;

    public Mono<String> getNews() {
        return newsAggregatorService.getAllNews();
    }
}
