package org.aminesidki.postprep.service.jwt;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.LoginRequestDTO;
import org.aminesidki.postprep.entity.AppUser;
import org.aminesidki.postprep.exception.Unauthorized;
import org.aminesidki.postprep.mapper.AppUserMapper;
import org.aminesidki.postprep.properties.JwtProperties;
import org.aminesidki.postprep.security.CustomUserDetails;
import org.aminesidki.postprep.security.CustomUserDetailsService;
import org.aminesidki.postprep.service.AppUserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final AppUserService appUserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

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
        AppUser user =appUserService.findUserByEmail(loginRequest.getEmail());
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new Unauthorized("Invalid Credentials");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        return generateToken(authentication);
    }

    public void logout(Authentication authentication) {
        if (authentication == null) {
            return;
        }
        String email = authentication.getName();
        appUserService.updateRefreshToken(email, null);
    }
}