package com.obsidi.feedapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.obsidi.feedapp.service.FeedService;
import com.obsidi.feedapp.jpa.Feed;

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
}
