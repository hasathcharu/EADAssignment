package org.ead.usermanagement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.usermanagement.dto.AuthenticationRequest;
import org.ead.usermanagement.dto.RegisterRequest;
import org.ead.usermanagement.response.AuthenticationResponse;
import org.ead.usermanagement.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        System.out.println("hi");
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
