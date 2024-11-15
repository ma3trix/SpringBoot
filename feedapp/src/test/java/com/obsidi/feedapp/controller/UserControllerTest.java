package com.obsidi.feedapp.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.obsidi.feedapp.jpa.User;
import com.obsidi.feedapp.repository.UserRepository;
import com.obsidi.feedapp.security.JwtService;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerTest {
    User user;
    String otherUsername;
    String otherPassword;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {

        this.user = new User();

        this.user.setFirstName("John");
        this.user.setLastName("Doe");
        this.user.setUsername("johndoe");
        this.user.setPassword("mypassword");
        this.user.setPhone("987654321");
        this.user.setEmailId("johndoe@example.com");

        this.otherUsername = "janedoe";
        this.otherPassword = "letmein";
    }

    @Test
    @Order(1)
    public void signupIntegrationTest() throws Exception {

        ObjectMapper objectMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();

        /* Check the Rest End Point Response */
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.firstName", is(this.user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(this.user.getLastName())))
                .andExpect(jsonPath("$.username", is(this.user.getUsername())))
                .andExpect(jsonPath("$.phone", is(this.user.getPhone())))
                .andExpect(jsonPath("$.emailId", is(this.user.getEmailId())));

        /* Check the DB */
        Optional<User> opt = this.userRepository.findByUsername(this.user.getUsername());

        assertTrue(opt.isPresent(), "User Should Exist");

        assertEquals(1, opt.get().getUserId());
        assertEquals(this.user.getFirstName(), opt.get().getFirstName());
        assertEquals(this.user.getLastName(), opt.get().getLastName());
        assertEquals(this.user.getUsername(), opt.get().getUsername());
        assertEquals(this.user.getPhone(), opt.get().getPhone());
        assertEquals(this.user.getEmailId(), opt.get().getEmailId());
        assertEquals(false, opt.get().getEmailVerified());

        assertTrue(this.passwordEncoder.matches(this.user.getPassword(), opt.get().getPassword()));
    }

    @Test
    @Order(2)
    public void signupUsernameExistsIntegrationTest() throws Exception {

        ObjectMapper objectMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();

        /* Check the Rest End Point Response */
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.user)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatusCode", is(400)))
                .andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("BAD REQUEST")))
                .andExpect(jsonPath("$.message",
                        is(String.format("Username already exists, %s", this.user.getUsername()))));
    }

    @Test
    @Order(3)
    public void signupEmailExistsIntegrationTest() throws Exception {

        ObjectMapper objectMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();

        this.user.setUsername(this.otherUsername);

        /* Check the Rest End Point Response */
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.user)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatusCode", is(400)))
                .andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("BAD REQUEST")))
                .andExpect(
                        jsonPath("$.message", is(String.format("Email already exists, %s", this.user.getEmailId()))));
    }

    @Test
    @Order(4)
    public void verifyEmailIntegrationTest() throws Exception {

        /* Check if the User exists */
        Optional<User> opt = this.userRepository.findByUsername(this.user.getUsername());
        assertTrue(opt.isPresent(), "User Should Exist");

        /* Check the user emailVerified flag initial value */
        assertEquals(false, opt.get().getEmailVerified());

        String jwt = String.format("Bearer %s", this.jwtService.generateJwtToken(this.user.getUsername(), 10_000));

        /* Check the Rest End Point Response */
        this.mockMvc.perform(MockMvcRequestBuilders.get("/user/verify/email").header(AUTHORIZATION, jwt))
                .andExpect(status().isOk());

        /* Check if the User exists */
        opt = this.userRepository.findByUsername(this.user.getUsername());
        assertTrue(opt.isPresent(), "User Should Exist");

        /* Check the user emailVerified flag was updated */
        assertEquals(true, opt.get().getEmailVerified());

    }

    @Test
    @Order(5)
    public void verifyEmailUsernameNotFoundIntegrationTest() throws Exception {

        /* Check if the User exists */
        Optional<User> opt = this.userRepository.findByUsername(this.otherUsername);
        assertTrue(opt.isEmpty(), "User Should Not Exist");

        String jwt = String.format("Bearer %s", this.jwtService.generateJwtToken(this.otherUsername, 10_000));

        /* Check the Rest End Point Response */
        this.mockMvc.perform(MockMvcRequestBuilders.get("/user/verify/email").header(AUTHORIZATION, jwt))
                .andExpect(status().is4xxClientError()).andExpect(jsonPath("$.httpStatusCode", is(400)))
                .andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("BAD REQUEST")))
                .andExpect(jsonPath("$.message", is(String.format("Username doesn't exist, %s", this.otherUsername))));

    }

    @Test
    @Order(6)
    public void loginIntegrationTest() throws Exception {

        /* Create object to generate JSON */
        ObjectNode root = this.objectMapper.createObjectNode();
        root.put("username", this.user.getUsername());
        root.put("password", this.user.getPassword());

        /* Check the Rest End Point Response */
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(root)))
                .andExpect(header().exists(AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(this.user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(this.user.getLastName())))
                .andExpect(jsonPath("$.username", is(this.user.getUsername())))
                .andExpect(jsonPath("$.phone", is(this.user.getPhone())))
                .andExpect(jsonPath("$.emailId", is(this.user.getEmailId())))
                .andExpect(jsonPath("$.emailVerified", is(true)));
    }

    @Test
    @Order(7)
    public void loginEmailNotVerifiedIntegrationTest() throws Exception {

        /* Create object to generate JSON */
        ObjectNode root = this.objectMapper.createObjectNode();
        root.put("username", this.user.getUsername());
        root.put("password", this.user.getPassword());

        /* Check if the User exists */
        Optional<User> opt = this.userRepository.findByUsername(this.user.getUsername());
        assertTrue(opt.isPresent(), "User Should Exist");

        /* Set user.emailVerified to false */
        opt.ifPresent(u -> {
            u.setEmailVerified(false);
            this.userRepository.save(u);
        });

        /* Check the Rest End Point Response */
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(root)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatusCode", is(400)))
                .andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("BAD REQUEST")))
                .andExpect(jsonPath("$.message",
                        is(String.format("Email requires verification, %s", this.user.getEmailId()))));

        /* Check if the User exists */
        opt = this.userRepository.findByUsername(this.user.getUsername());
        assertTrue(opt.isPresent(), "User Should Exist");

    }
}
