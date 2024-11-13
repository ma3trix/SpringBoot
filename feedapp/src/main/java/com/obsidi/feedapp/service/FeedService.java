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
import com.obsidi.feedapp.repository.FeedMetaDataRepository;
import com.obsidi.feedapp.jpa.Feed;
import com.obsidi.feedapp.jpa.FeedMetaData;
import com.obsidi.feedapp.jpa.User;
import com.obsidi.feedapp.domain.PageResponse;
import com.obsidi.feedapp.exception.domain.FeedNotFoundException;
import com.obsidi.feedapp.exception.domain.FeedNotUserException;
import com.obsidi.feedapp.exception.domain.LikeExistException;
import com.obsidi.feedapp.exception.domain.UserNotFoundException;

import org.springframework.data.domain.Page;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class FeedService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private FeedMetaDataRepository feedMetaDataRepository; // Inject FeedMetaDataRepository

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

        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

        Page<Feed> paged = this.feedRepository.findByUserNot(user,
                PageRequest.of(pageNum, pageSize, Sort.by("feedId").descending()));

        return new PageResponse<>(paged);
    }

    public FeedMetaData createFeedMetaData(int feedId, FeedMetaData meta) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

        Feed feed = this.feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedNotFoundException(String.format("Feed doesn't exist, %d", feedId)));

        FeedMetaData newMeta = new FeedMetaData();
        newMeta.setIsLike(false);
        newMeta.setUser(user);
        newMeta.setFeed(feed);
        newMeta.setCreatedOn(Timestamp.from(Instant.now()));

        if (Optional.ofNullable(meta.getIsLike()).isPresent()) {
            newMeta.setIsLike(meta.getIsLike());

            if (meta.getIsLike()) {
                boolean alreadyLiked = feed.getFeedMetaData().stream()
                        .anyMatch(
                                m -> m.getUser().getUsername().equals(username) && Boolean.TRUE.equals(m.getIsLike()));

                if (alreadyLiked) {
                    throw new LikeExistException(
                            String.format("Feed already liked, feedId: %d, username: %s", feedId, username));
                }
                newMeta.setComment(""); // Set comment to empty if it's a like
            }
        }

        if (!newMeta.getIsLike()) {
            newMeta.setComment(meta.getComment());
        }

        return this.feedMetaDataRepository.save(newMeta); // Now correctly references feedMetaDataRepository
    }

    public void deleteFeed(int feedId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Feed feed = this.feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedNotFoundException(String.format("Feed doesn't exist, %d", feedId)));

        Optional.of(feed).filter(f -> f.getUser().getUsername().equals(username))
                .orElseThrow(() -> new FeedNotUserException(String
                        .format("Feed doesn't belong to current User, feedId: %d, username: %s", feedId, username)));

        this.feedRepository.delete(feed);
    }

}