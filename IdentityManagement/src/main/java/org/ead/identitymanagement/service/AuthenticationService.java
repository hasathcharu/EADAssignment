package org.ead.identitymanagement.service;

import lombok.RequiredArgsConstructor;
import org.ead.identitymanagement.config.JwtService;
import org.ead.identitymanagement.dto.AuthenticationRequest;
import org.ead.identitymanagement.dto.RegisterRequest;
import org.ead.identitymanagement.models.Role;
import org.ead.identitymanagement.models.User;
import org.ead.identitymanagement.repository.UserRepository;
import org.ead.identitymanagement.response.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        User checkUser = repository.findByEmail(request.getEmail()).orElse(null);
        if(checkUser!=null){
            return "User exists";
        }
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        return "User Registered";
//        var jwtToken = jwtService.generateToken(user);
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

}
