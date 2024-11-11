package com.obsidi.feedapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import java.util.Arrays;
import org.springframework.security.web.util.matcher.RequestMatcher;
import com.obsidi.feedapp.filter.CustomAuthEntryPoint;
import com.obsidi.feedapp.filter.JwtAuthorizationFilter;
import com.obsidi.feedapp.provider.ResourceProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    ResourceProvider provider;

    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    CustomAuthEntryPoint customAuthEntryPoint;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    private RequestMatcher[] getMatchers(MvcRequestMatcher.Builder mvc) {
        return Arrays.stream(this.provider.getJwtExcludedUrls())
                .map(url -> mvc.pattern(url))
                .toArray(RequestMatcher[]::new);
    }

    @Bean("test")
    SecurityFilterChain securityFilterChainTest(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers(this.getMatchers(mvc)).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling((handler) -> handler.authenticationEntryPoint(this.customAuthEntryPoint))
                .addFilterBefore(this.jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers((headers) -> headers.frameOptions((frame) -> frame.sameOrigin()))
                .cors(withDefaults())
                .csrf((csrf) -> csrf.disable());

        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(this.getMatchers(mvc)).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling((handler) -> handler.authenticationEntryPoint(this.customAuthEntryPoint))
                .addFilterBefore(this.jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(withDefaults())
                .csrf((csrf) -> csrf.disable());

        return http.build();
    }
}
