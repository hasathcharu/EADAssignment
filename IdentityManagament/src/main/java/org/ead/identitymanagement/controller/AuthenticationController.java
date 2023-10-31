package org.ead.identitymanagement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.identitymanagement.dto.AuthenticationRequest;
import org.ead.identitymanagement.dto.RegisterRequest;
import org.ead.identitymanagement.response.AuthenticationResponse;
import org.ead.identitymanagement.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        System.out.println("hi");
        return ResponseEntity.ok(service.authenticate(request));
    }
}
