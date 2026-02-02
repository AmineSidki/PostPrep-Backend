package org.aminesidki.postprep.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aminesidki.postprep.entity.AppUser;
import org.aminesidki.postprep.mapper.AppUserMapper;
import org.aminesidki.postprep.repository.AppUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthenticationManager implements AuthenticationManager {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserMapper userMapper;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AppUser user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException(authentication.getName()));

        if (passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            log.debug("Authentication successful");
            CustomUserDetails userDetails = new CustomUserDetails(userMapper.toDto(user));

            return new UsernamePasswordAuthenticationToken(user.getEmail(), null, userDetails.getAuthorities());
        } else {
            log.debug("Authentication failed");
            throw new BadCredentialsException("Bad credentials");
        }
    }
}
