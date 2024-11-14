package com.obsidi.feedapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.obsidi.feedapp.service.FeedService;
import com.obsidi.feedapp.jpa.Feed;
import com.obsidi.feedapp.domain.PageResponse;

@CrossOrigin
@RestController
@RequestMapping("/feeds")
public class FeedController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FeedService feedService;

    @PostMapping
    public Feed createFeed(@RequestBody Feed feed) {
        logger.debug("Creating Feed");
        return this.feedService.createFeed(feed);
    }

    @GetMapping("/{feedId}")
    public Feed getFeed(@PathVariable int feedId) {
        logger.debug("Fetching Feed with ID: {}", feedId);
        return feedService.getFeedById(feedId);
    }

    @GetMapping("/user/{pageNum}/{pageSize}")
    public PageResponse<Feed> getUserFeeds(@PathVariable int pageNum, @PathVariable int pageSize) {
        logger.debug("Getting User Feeds List, pageNum: {}, pageSize: {}", pageNum, pageSize);
        return this.feedService.getUserFeeds(pageNum, pageSize);
    }

    @GetMapping("/other/{pageNum}/{pageSize}")
    public PageResponse<Feed> getOtherUsersFeeds(@PathVariable int pageNum, @PathVariable int pageSize) {
        logger.debug("Getting Feeds from Other Users, pageNum: {}, pageSize: {}", pageNum, pageSize);
        return this.feedService.getOtherUsersFeeds(pageNum, pageSize);
    }

    @DeleteMapping("/{feedId}")
    public void deleteFeed(@PathVariable int feedId) {

        logger.debug("Deleting Feed, feedId: {}", feedId);

        this.feedService.deleteFeed(feedId);
    }
}
