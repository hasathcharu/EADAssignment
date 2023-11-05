package org.ead.identitymanagement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.identitymanagement.dto.AuthenticationRequest;
import org.ead.identitymanagement.dto.RegisterRequest;
import org.ead.identitymanagement.response.AuthenticationResponse;
import org.ead.identitymanagement.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;

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
        System.out.println("hi");
        return ResponseEntity.ok(service.authenticate(request));
    }
}
