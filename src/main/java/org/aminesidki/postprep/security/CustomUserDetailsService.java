package org.aminesidki.postprep.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.AppUserDTO;
import org.aminesidki.postprep.mapper.AppUserMapper;
import org.aminesidki.postprep.service.AppUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserService appUserService;

    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        AppUserDTO appUser = appUserService.findByEmailWithArticles(email);
        return new CustomUserDetails(appUser);
    }
}
