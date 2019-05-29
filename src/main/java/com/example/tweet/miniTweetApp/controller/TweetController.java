package com.example.tweet.miniTweetApp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.tweet.miniTweetApp.models.Tweet;
import com.example.tweet.miniTweetApp.repos.TweetRepo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class TweetController {
	@Autowired
	TweetRepo tweetRepo;

	@GetMapping("/tweets")
	public Flux<Tweet> getAllTweets() {
		return tweetRepo.findAll();
	}

	@PostMapping("/tweets")
	public Mono<Tweet> createTweet(@Valid @RequestBody Tweet tweet) {
		 return tweetRepo.save(tweet);
	}

	@GetMapping("/tweets/{tweetId}")
	public Mono<ResponseEntity<Tweet>> getTweetById(@PathVariable(value = "tweetId") String tweetId) {
		return tweetRepo.findById(tweetId).map(savedTweet -> ResponseEntity.ok(savedTweet))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PutMapping("/tweets/{id}")
	public Mono<ResponseEntity<Tweet>> updateTweet(@PathVariable(value = "id") String tweetId, 
			@Valid @RequestBody Tweet tweet) {
		return tweetRepo.findById(tweetId).flatMap(existingTweet -> {
			existingTweet.setText(tweet.getText());
			return tweetRepo.save(existingTweet);
		}).map(updatedTweet -> new ResponseEntity<>(updatedTweet, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	@DeleteMapping("/tweets/{id}")
	public Mono<ResponseEntity<Void>> deleteTweet(@PathVariable(value = "id") String id){
		return tweetRepo.findById(id).flatMap(tweet -> 
			tweetRepo.delete(tweet).then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
		).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
	@GetMapping(value = "/stream/tweets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Tweet> streamAllTweets(){
		return tweetRepo.findWithTailableCursorBy();
	}
}
