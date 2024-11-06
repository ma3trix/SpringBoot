package com.obsidi.feedapp.jpa;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.persistence.CascadeType;

@Entity
@Table(name = "\"Feed\"")
public class Feed implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"feedId\"")
    private Integer feedId;

    @Column(columnDefinition = "TEXT")
    private String picture;

    private String content;

    @Column(name = "\"createdOn\"")
    private Timestamp createdOn;

    @ManyToOne
    @JoinColumn(name = "\"userId\"")
    private User user;

    @JsonInclude(Include.NON_NULL)
    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<FeedMetaData> feedMetaData;

    public Feed() {
    }

    public Integer getFeedId() {
        return this.feedId;
    }

    public void setFeedId(Integer feedId) {
        this.feedId = feedId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Timestamp getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<FeedMetaData> getFeedMetaData() {
        return this.feedMetaData;
    }

    public void setFeedMetaData(List<FeedMetaData> feedMetaData) {
        this.feedMetaData = feedMetaData;
    }

    @Override
    public String toString() {
        return "Feed [feedId=" + feedId + ", picture=" + picture + ", content=" + content + ", createdOn=" + createdOn
                + "]";
    }

}