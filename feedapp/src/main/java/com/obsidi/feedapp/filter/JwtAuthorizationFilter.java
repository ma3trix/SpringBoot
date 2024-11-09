package com.obsidi.feedapp.filter;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.obsidi.feedapp.provider.ResourceProvider;
import com.obsidi.feedapp.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.OPTIONS;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ResourceProvider provider;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    private Authentication getAuthentication(String username, HttpServletRequest req) {
        UsernamePasswordAuthenticationToken userPasswordAuthToken = new UsernamePasswordAuthenticationToken(username,
                null, null);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        return userPasswordAuthToken;
    }

    private boolean isJwtPrefixValid(String header) {
        logger.debug("Authorization Header: {}", Optional.ofNullable(header).orElse("Not Present"));
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(this.provider.getJwtPrefix()))
                .isPresent();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("Running Jwt Filter, URL: {}, Method: {}", req.getRequestURL(), req.getMethod());

        try {
            if (!req.getMethod().equalsIgnoreCase(OPTIONS.name())) {
                String header = req.getHeader(AUTHORIZATION);

                if (this.isJwtPrefixValid(header)) {
                    String username = this.jwtService.getSubject(header.substring(7));
                    SecurityContextHolder.getContext().setAuthentication(this.getAuthentication(username, req));
                    logger.debug("User Authorized: {}", username);
                }
            }

            filterChain.doFilter(req, res);
        } catch (JWTVerificationException ex) {
            logger.debug("Token cannot be verified, Reason: {}", ex.getMessage());
            this.resolver.resolveException(req, res, null, ex);
        }
    }
}