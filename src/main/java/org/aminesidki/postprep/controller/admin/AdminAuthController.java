package org.aminesidki.postprep.controller.admin;

import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.LoginRequestDTO;
import org.aminesidki.postprep.entity.AppUser;
import org.aminesidki.postprep.properties.JwtProperties;
import org.aminesidki.postprep.repository.AppUserRepository;
import org.aminesidki.postprep.service.jwt.Token;
import org.aminesidki.postprep.service.jwt.TokenService;
import org.aminesidki.postprep.utils.CookieUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final TokenService tokenService;
    private final AppUserRepository appUserRepository;
    private final JwtProperties jwtProperties;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody @Valid LoginRequestDTO loginRequest) {
        Token token = tokenService.login(loginRequest);
        AppUser user = appUserRepository.findByEmail(loginRequest.getEmail()).orElseThrow();

        if (!user.getRole().name().equals("ADMIN") && !user.getRole().name().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body(Map.of("error", "Access Denied: Restricted to administrators"));
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("access_token", token.getAccess_token(), jwtProperties.accessTokenExpirationDuration().toSeconds(), "/").toString())
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("refresh_token", token.getRefresh_token(), jwtProperties.refreshTokenExpirationDuration().toSeconds(), "/api/v1/auth/refresh").toString())
                .body(Map.of(
                        "message", "Admin logged in successfully",
                        "role", "ADMIN"
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {

        tokenService.logout(authentication);

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true).secure(true).path("/").maxAge(0).sameSite("Strict").build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(true).path("/api/v1/auth/refresh").maxAge(0).sameSite("Strict").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(Map.of("message", "Logged out successfully"));
    }

}