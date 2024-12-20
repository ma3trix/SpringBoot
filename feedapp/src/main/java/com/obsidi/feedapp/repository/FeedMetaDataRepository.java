package com.obsidi.feedapp.repository;

import com.obsidi.feedapp.jpa.FeedMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedMetaDataRepository extends JpaRepository<FeedMetaData, Integer> {

    public class LikeExistException extends RuntimeException {

        public LikeExistException(String message) {

            super(message);

        }

    }

}
