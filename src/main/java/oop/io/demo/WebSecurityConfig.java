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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.userdetails.User;

import oop.io.demo.auth.security.jwt.AuthEntryPointJwt;
import oop.io.demo.auth.security.jwt.AuthTokenFilter;
import oop.io.demo.auth.security.services.UserDetailServiceImplementation;
import oop.io.demo.user.USERTYPE;

@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(
    // securedEnabled = true,
    // jsr250Enabled = true,
    prePostEnabled = true)
public class WebSecurityConfig {
    @Value("${spring.h2.console.path}")
    private String h2ConsolePath;
    
    @Autowired
    UserDetailServiceImplementation userDetailsService;
  
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
      return new AuthTokenFilter();
    }
    
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
      http.cors().and().csrf().disable()
          .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
             .authorizeRequests().antMatchers("/auth/**").permitAll()
             .antMatchers("/password/**").permitAll()
             .antMatchers(h2ConsolePath + "/**").permitAll();
          // .antMatchers("/usermanagement/**").hasAnyAuthority(USERTYPE.ADMIN.toString())
          // .antMatchers("/user").hasAnyAuthority(USERTYPE.ADMIN.toString())
          // .antMatchers("/user/editprofile").hasAnyAuthority(only the user themself)
          // .antMatchers("/user/userbyusername").hasAnyAuthority(admin or the user themself)
          // .antMatchers("/passmanagement/**").hasAnyAuthority(USERTYPE.ADMIN.toString())
          // .antMatchers("/pass/**").hasAnyAuthority(USERTYPE.ADMIN.toString(), USERTYPE.STAFF.toString())
          // .antMatchers("/attractionmanagement/**").hasAnyAuthority(USERTYPE.ADMIN.toString())
          // .antMatchers("/attraction/**").hasAnyAuthority(USERTYPE.ADMIN.toString(), USERTYPE.STAFF.toString())
          // .antMatchers("/send").hasAnyAuthority(@yeoshi?)
          // .antMatchers("/book/**").permitAll()
          // .anyRequest().authenticated();
          // .and().formLogin().loginPage("/login").failureUrl("/login?error=true")
          // .defaultSuccessUrl("/home", true)
          // .usernameParameter("email")
          // .passwordParameter("password")
          // .and().logout()
          // .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
          // .logoutSuccessUrl("/login")
  
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
      
        return http.build();
    }
}
