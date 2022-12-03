/**
 * Contains method for setting user authentication
 */

package oop.io.demo.auth.security.jwt;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import oop.io.demo.auth.security.services.UserDetailServiceImplementation;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
  
    @Autowired
    private UserDetailServiceImplementation userDetailService;
  
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

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
      try {
        String jwt = cookieAuth.get().getValue();
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String email = jwtUtils.getEmailFromJwtToken(jwt);
  
          UserDetails userDetails = userDetailService.loadUserByUsername(email);
          
          UsernamePasswordAuthenticationToken authentication = 
              new UsernamePasswordAuthenticationToken(userDetails,
                                                      null,
                                                      userDetails.getAuthorities());
          
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
  
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (Exception e) {
        logger.error("Cannot set user authentication: {}", e);
      }
      
      filterChain.doFilter(request, response);
    }
}
