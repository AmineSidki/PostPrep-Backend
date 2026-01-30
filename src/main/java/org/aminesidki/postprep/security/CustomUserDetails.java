package org.aminesidki.postprep.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aminesidki.postprep.dto.AppUserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    AppUserDTO appUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = appUser.getRole();
        return (role != null)
                ? List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                : List.of();
    }

    @Override
    public String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        return appUser.getEmail();
    }
}
