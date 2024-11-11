package com.obsidi.feedapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.obsidi.feedapp.provider.ResourceProvider;
import org.h2.tools.Server;
import java.sql.SQLException;

@Configuration
@Profile("test")
public class WebTestConfig {

    @Autowired
    ResourceProvider resourceProvider;

    @Bean(initMethod = "start", destroyMethod = "stop")
    Server inMemoryH2DatabaseServer() throws SQLException {
        return Server.createTcpServer(this.resourceProvider.getH2ServerParams());
    }
}
