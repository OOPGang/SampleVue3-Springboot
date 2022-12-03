package oop.io.demo.auth.security.cookie;


import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

public class CookieAuthenticationFilter extends OncePerRequestFilter{


    public static final String COOKIE_NAME = "oop";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        Optional<Cookie> cookieAuth = Stream.of(Optional.ofNullable(request.getCookies())
        .orElse(new Cookie[0]))
        .filter(cookie-> COOKIE_NAME.equals(cookie.getName()))
        .findFirst();

        cookieAuth.ifPresent(cookie ->
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(cookie.getValue(), null))
        );

        filterChain.doFilter(request, response);
    }
    
    
}

