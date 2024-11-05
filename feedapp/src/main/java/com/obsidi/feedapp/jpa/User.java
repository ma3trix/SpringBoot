package com.obsidi.feedapp.jpa;

import java.io.Serializable;
import java.sql.Timestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@Table(name = "\"User\"")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"userId\"")
    private Integer userId;

    @Column(name = "\"firstName\"")
    private String firstName;

    @Column(name = "\"lastName\"")
    private String lastName;

    @Column(unique = true)
    private String username;

    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    private String phone;

    @Column(name = "\"emailId\"", unique = true)
    private String emailId;

    @Column(name = "\"emailVerified\"")
    private Boolean emailVerified;

    @Column(name = "\"createdOn\"")
    private Timestamp createdOn;

    // No-arg constructor
    public User() {
    }

    // Getters and Setters

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    // toString method
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", emailId='" + emailId + '\'' +
                ", emailVerified=" + emailVerified +
                ", createdOn=" + createdOn +
                '}';
    }
}