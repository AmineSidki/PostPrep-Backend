package org.aminesidki.postprep.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.LoginRequestDTO;
import org.aminesidki.postprep.dto.RegisterRequestDTO;
import org.aminesidki.postprep.entity.AppUser;
import org.aminesidki.postprep.properties.JwtProperties;
import org.aminesidki.postprep.security.CustomUserDetails;
import org.aminesidki.postprep.service.AppUserService;
import org.aminesidki.postprep.service.jwt.Token;
import org.aminesidki.postprep.service.jwt.TokenService;
import org.aminesidki.postprep.utils.CookieUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final TokenService tokenService;
    private final AppUserService appUserService;
    private final JwtProperties jwtProperties;
    private final AppUserService userService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequestDTO loginRequest) {
        Token token = tokenService.login(loginRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("access_token", token.getAccess_token(), jwtProperties.accessTokenExpirationDuration().toSeconds(), "/").toString())
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("refresh_token", token.getRefresh_token(), jwtProperties.refreshTokenExpirationDuration().toSeconds(), "/api/v1/auth/refresh").toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = true) String refreshToken) {
        Token token = tokenService.refreshToken(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("access_token", token.getAccess_token(),  jwtProperties.accessTokenExpirationDuration().toSeconds(), "/").toString())
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("refresh_token", token.getRefresh_token(), jwtProperties.refreshTokenExpirationDuration().toSeconds(), "/api/v1/auth/refresh").toString())
                .body(Map.of("message", "Refresh in successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestDTO request) {
        appUserService.register(request);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal != null && principal.getAppUser() != null) {
            appUserService.logout(principal.getAppUser().getId());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("access_token", "", 0, "/").toString())
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("refresh_token", "", 0, "/api/v1/auth/refresh").toString())
                .body(Map.of("message", "Logged out in successfully"));
    }

}