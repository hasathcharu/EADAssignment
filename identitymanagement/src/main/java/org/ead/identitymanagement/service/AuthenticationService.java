package org.ead.identitymanagement.service;

import lombok.RequiredArgsConstructor;
import org.ead.identitymanagement.config.JwtService;
import org.ead.identitymanagement.dto.AuthenticationRequest;
import org.ead.identitymanagement.dto.CreateUserDTO;
import org.ead.identitymanagement.dto.RegisterRequest;
import org.ead.identitymanagement.exception.RestException;
import org.ead.identitymanagement.models.Role;
import org.ead.identitymanagement.models.User;
import org.ead.identitymanagement.repository.UserRepository;
import org.ead.identitymanagement.response.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final WebClient.Builder webClientBuilder;
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

        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .address(request.getAddress())
                .gender(request.getGender())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .name(request.getName())
                .build();
        String res = webClientBuilder.build()
                .post()
                .uri("http://usermanagement/api/user")
                .bodyValue(createUserDTO)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.out.println(e.getMessage());
                    throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to inventory management");
                })
                .block();

        if(Objects.equals(res, "Success")){
            return "User Registered";
        }
        throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to register");
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }catch(DisabledException e){
            throw new RestException(HttpStatus.UNAUTHORIZED, "User is disabled");
        }
        catch(LockedException e){
            throw new RestException(HttpStatus.UNAUTHORIZED, "User is locked");
        }
        catch(BadCredentialsException e){
            throw new RestException(HttpStatus.UNAUTHORIZED, "Invalid Credentials");
        }

        var user = repository.findByEmail(request.getEmail());

        if(user.isPresent()){
            var jwtToken = jwtService.generateToken(user.get());
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
        throw new RestException(HttpStatus.UNAUTHORIZED,"Invalid Credentials");
    }

}
