package oop.io.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.userdetails.User;

import oop.io.demo.auth.security.cookie.AuthEntryPoint;
import oop.io.demo.auth.security.cookie.CookieAuthenticationFilter;
import oop.io.demo.auth.security.jwt.AuthEntryPointJwt;
import oop.io.demo.auth.security.services.UserDetailServiceImplementation;
import oop.io.demo.user.USERTYPE;

@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled = true)
public class WebSecurityConfig {
    @Value("${spring.h2.console.path}")
    private String h2ConsolePath;
    
    @Autowired
    UserDetailServiceImplementation userDetailsService;
  
    // @Autowired
    // private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private AuthEntryPoint unauthorizedHandler;

    // @Bean
    // public AuthTokenFilter authenticationJwtTokenFilter() {
    //   return new AuthTokenFilter();
    // }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
      return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }
    
    //Create new user to use locally
    @Bean
    public InMemoryUserDetailsManager createUser() {
        var m = new InMemoryUserDetailsManager();
        m.createUser(User.withUsername("admin@sss.com").password(passwordEncoder().encode("123")).roles("ADMIN").build());
        return m;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
          .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
          .and()
          .addFilterBefore(new CookieAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
          .cors().and().csrf().disable()
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and().logout()
              .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
              .logoutSuccessUrl("/login").deleteCookies(CookieAuthenticationFilter.COOKIE_NAME)
          
          .and().authorizeRequests()

            //  .antMatchers(h2ConsolePath + "/**").permitAll()
            //FRONTENDENDPOINTS
            .antMatchers("/analytics").hasAnyAuthority(USERTYPE.ADMIN.toString())
            .antMatchers("/corporate-pass-creation").hasAnyAuthority(USERTYPE.ADMIN.toString())
            .antMatchers("/corporate-pass-management").hasAnyAuthority(USERTYPE.ADMIN.toString())
            .antMatchers("/users").hasAnyAuthority(USERTYPE.ADMIN.toString())
            .antMatchers("/gopass").hasAnyAuthority(USERTYPE.GENERALOFFICE.toString())
            .antMatchers("/booking").hasAnyAuthority(USERTYPE.ADMIN.toString(),USERTYPE.ADMIN.toString())
            .antMatchers("/pass-details").hasAnyAuthority(USERTYPE.ADMIN.toString(),USERTYPE.ADMIN.toString())
            .antMatchers("/corporate-pass-management/passes/**").hasAnyAuthority(USERTYPE.ADMIN.toString())
            .antMatchers("/login").permitAll()
            .antMatchers("/register").permitAll()
            .antMatchers("/authentication").permitAll();

            //BACKENDENDPOINTS
            // .antMatchers("/uploadcsv/**").hasAnyAuthority(USERTYPE.ADMIN.toString())

            // .antMatchers("/export/**").hasAnyAuthority(USERTYPE.ADMIN.toString())

            // .antMatchers("/analysis/**").hasAnyAuthority(USERTYPE.ADMIN.toString())

            // .antMatchers("/usermanagement/**").hasAnyAuthority(USERTYPE.ADMIN.toString())
            // .antMatchers("/user/editprofile").hasAnyAuthority(USERTYPE.STAFF.toString())
            // .antMatchers("/user/userbyusername").hasAnyAuthority(USERTYPE.ADMIN.toString(), USERTYPE.STAFF.toString())
            // .antMatchers("/user/**").hasAnyAuthority(USERTYPE.ADMIN.toString(), USERTYPE.STAFF.toString())
            
            // .antMatchers("/passmanagement/**").hasAnyAuthority(USERTYPE.ADMIN.toString())
            // .antMatchers("/pass/**").hasAnyAuthority(USERTYPE.ADMIN.toString(), USERTYPE.STAFF.toString())
            // .antMatchers("/passstatus/**").hasAnyAuthority(USERTYPE.GENERALOFFICE.toString())
            
            // .antMatchers("/attractionmanagement/**").hasAnyAuthority("ROLE_ADMIN")
            // .antMatchers("/attraction/**").hasAnyAuthority(USERTYPE.ADMIN.toString(), USERTYPE.STAFF.toString())

            // .antMatchers("/loan/delete/**").hasAnyAuthority(USERTYPE.ADMIN.toString())
            // .antMatchers("/loan/**").hasAnyAuthority(USERTYPE.ADMIN.toString(), USERTYPE.STAFF.toString())

            // .antMatchers("/email/**").permitAll()

            // .antMatchers("/auth/**").permitAll()
            // .antMatchers("/password/**").permitAll();
            //   .anyRequest().authenticated()
            // .and().formLogin().loginPage("/login").failureUrl("/login?error=true")
            //   .defaultSuccessUrl("/home", true)
            //   .usernameParameter("email")
            //   .passwordParameter("password")
            // .and().logout()
            //   .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            //   .logoutSuccessUrl("/login")
            //   .invalidateHttpSession(true).permitAll();
        
        return http.build();
    }
}
