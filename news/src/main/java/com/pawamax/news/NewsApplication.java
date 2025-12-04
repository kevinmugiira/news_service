package com.pawamax.news;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableScheduling
@SpringBootApplication
//        (exclude = {
//        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
//        org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration.class
//})
public class NewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsApplication.class, args);
	}

}
