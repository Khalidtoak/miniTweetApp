package com.example.tweet.miniTweetApp.repos;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;

import com.example.tweet.miniTweetApp.models.Tweet;

import reactor.core.publisher.Flux;
@Repository
public interface TweetRepo extends ReactiveMongoRepository<Tweet, String>{
	@Tailable
	public Flux<Tweet> findWithTailableCursorBy();

}
