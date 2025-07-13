package com.alpopan.taskmaster.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtProvider;
    private final UserDetailsService uds;

    public AuthController(AuthenticationManager am,
                          JwtTokenProvider jp,
                          UserDetailsService uds) {
        this.authManager = am;
        this.jwtProvider = jp;
        this.uds = uds;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody AuthRequest req) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(), req.getPassword()
                )
        );

        UserDetails user = uds.loadUserByUsername(req.getUsername());

        String token = jwtProvider.createToken(
                user.getUsername(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );

        return ResponseEntity.ok(
                Map.of("username", user.getUsername(), "token", token)
        );
    }
}
