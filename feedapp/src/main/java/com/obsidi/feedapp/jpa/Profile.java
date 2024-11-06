package com.obsidi.feedapp.jpa;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@Table(name = "\"Profile\"")
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"profileId\"")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer profileId;

    private String bio;
    private String city;
    private String country;
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String picture;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "\"userId\"")
    private User user;

    public Profile() {
    }

    public Integer getProfileId() {
        return this.profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadline() {
        return this.headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getPicture() {
        return this.picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Profile [bio=" + bio + ", city=" + city + ", country=" + country + ", headline=" + headline
                + ", picture="
                + picture + ", profileId=" + profileId + ", user=" + user + "]";
    }

}
