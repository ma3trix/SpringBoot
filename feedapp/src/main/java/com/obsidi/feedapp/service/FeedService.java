package com.obsidi.feedapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import java.sql.Timestamp;
import java.time.Instant;
import com.obsidi.feedapp.repository.FeedRepository;
import com.obsidi.feedapp.repository.UserRepository;
import com.obsidi.feedapp.exception.FeedNotFoundException;
import com.obsidi.feedapp.exception.UserNotFoundException;
import com.obsidi.feedapp.jpa.Feed;
import com.obsidi.feedapp.jpa.User;
import com.obsidi.feedapp.domain.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class FeedService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedRepository feedRepository;

    public Feed createFeed(Feed feed) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

        feed.setUser(user);
        feed.setCreatedOn(Timestamp.from(Instant.now()));

        return this.feedRepository.save(feed);
    }

    public Feed getFeedById(int feedId) {
        return this.feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedNotFoundException(String.format("Feed doesn't exist, %d", feedId)));
    }

    public PageResponse<Feed> getUserFeeds(int pageNum, int pageSize) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

        Page<Feed> paged = this.feedRepository.findByUser(user,
                PageRequest.of(pageNum, pageSize, Sort.by("feedId").descending()));

        return new PageResponse<>(paged);
    }

    public PageResponse<Feed> getOtherUsersFeeds(int pageNum, int pageSize) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find the currently authenticated user
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

        // Retrieve feeds from other users
        Page<Feed> paged = this.feedRepository.findByUserNot(user,
                PageRequest.of(pageNum, pageSize, Sort.by("feedId").descending()));

        return new PageResponse<>(paged);
    }
}
