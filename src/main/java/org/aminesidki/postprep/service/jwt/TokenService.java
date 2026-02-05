package org.aminesidki.postprep.service.jwt;

import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.LoginRequestDTO;
import org.aminesidki.postprep.entity.AppUser;
import org.aminesidki.postprep.exception.Unauthorized;
import org.aminesidki.postprep.properties.JwtProperties;
import org.aminesidki.postprep.repository.AppUserRepository;
import org.aminesidki.postprep.repository.projection.AuthInfo;
import org.aminesidki.postprep.security.CustomUserDetails;
import org.aminesidki.postprep.security.CustomUserDetailsService;
import org.aminesidki.postprep.service.AppUserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final AppUserService appUserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;

    public Token generateToken(Authentication authentication) {
        Instant now = Instant.now();

        String role = authentication.getAuthorities().iterator().next().getAuthority();

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.accessTokenExpirationDuration()))
                .subject(authentication.getName())
                .claim("scope", role)
                .build();

        JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.refreshTokenExpirationDuration()))
                .subject(authentication.getName())
                .build();

        String accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();
        String refreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();

        appUserService.updateRefreshToken(authentication.getName(), refreshToken);

        return Token.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
    }

    public Token refreshToken(String refreshToken) {
        AppUser user = appUserService.findByRefreshToken(refreshToken);

        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user.getEmail());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, userDetails.getAuthorities());

        return generateToken(authentication);
    }

    public Token login(LoginRequestDTO loginRequest) {
        AppUser user = appUserService.findUserByEmail(loginRequest.getEmail());
        AuthInfo authInfo = appUserRepository.findAuthInfoByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new Unauthorized("Invalid Credentials");
        }

        UserDetails userDetails = User.builder()
                .username(authInfo.getEmail())
                .password(authInfo.getPassword())
                .roles(authInfo.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        return generateToken(authentication);
    }

}