package org.ead.identitymanagement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.identitymanagement.dto.AuthenticationRequest;
import org.ead.identitymanagement.dto.RegisterRequest;
import org.ead.identitymanagement.response.AuthenticationResponse;
import org.ead.identitymanagement.response.AuthorizationResponse;
import org.ead.identitymanagement.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/public")
public class AuthenticationControllerPublic {

    private final AuthService service;

    @PostMapping("/register")
    public String register(
            @RequestBody RegisterRequest request
    ){
        return service.register(request);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/authorize")
    public AuthorizationResponse authorize(
            @RequestHeader ("Authorization") String header
    ){
        System.out.println(header);
        return service.authorization(header);
    }
}
