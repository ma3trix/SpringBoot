package com.obsidi.feedapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.DisplayName;
import org.h2.tools.Server;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@ActiveProfiles("test")
class FeedAppApplicationTests {

	@MockBean
	Server server;

	@DisplayName("Demo Test")
	@Test
	void contextLoads() {
	}

}
