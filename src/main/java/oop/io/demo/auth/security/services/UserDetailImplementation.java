/**
 * Class to create a UserDetailImplementation from a User with SimpleGrantedAuthority attribute
 * that can be used to grant privileged access
 */
package oop.io.demo.auth.security.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import oop.io.demo.user.USERTYPE;
import oop.io.demo.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailImplementation implements UserDetails {
    private static final String serialVersionUID = "";


    private String email;
  
    @JsonIgnore
    private String password;

    private String name;
    
    GrantedAuthority authority;

    private boolean isVerified;

    public UserDetailImplementation(String email, String password,
    String name, GrantedAuthority authority, boolean isVerified) {
      this.email = email;
      this.password = password;
      this.authority = authority;
      this.isVerified = isVerified;
      this.name = name;
    }
  
    public static UserDetailImplementation build(User user) {
        String uType = user.getUserType()==null? USERTYPE.STAFF.toString(): user.getUserType().name();
        GrantedAuthority authority = new SimpleGrantedAuthority(uType);
  
        return new UserDetailImplementation(
            user.getEmail(),
            user.getPassword(), 
            user.getName(),
            authority,
            user.isVerified());
    }
  
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        return authorities;
    }

    public GrantedAuthority getAuthority() {
        return authority;
    }

    public String getName() {
        return name;
    }
  
    @Override
    public String getPassword() {
        return password;
    }
  
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
  
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
  
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
  
    @Override
    public boolean isEnabled() {
        return isVerified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailImplementation user = (UserDetailImplementation) o;
        return Objects.equals(email, user.email);
    }
    //this is here because it has to be overriden
    @Override
    public String getUsername() {
        return email;
    }
}
