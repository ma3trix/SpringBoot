package com.obsidi.feedapp.controller;

import org.slf4j.logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/")
    String testController() {

        System.out.println("Hello World!");

        return "Hello World!";
    }

}

// @RestController
// @RequestMapping("/user")

// public class UserController {

// final Logger logger = LoggerFactory.getLogger(this.getClass());

// @GetMapping("/test")
// String testController() {
// logger.debug("The testController() method was invoked!");
// return "The FeedApp application is up and running";
// }

// }
