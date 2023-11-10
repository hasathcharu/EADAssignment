package org.ead.identitymanagement.service;

import lombok.RequiredArgsConstructor;
import org.ead.identitymanagement.config.JwtService;
import org.ead.identitymanagement.dto.*;
import org.ead.identitymanagement.exception.RestException;
import org.ead.identitymanagement.models.Role;
import org.ead.identitymanagement.models.User;
import org.ead.identitymanagement.repository.UserRepository;
import org.ead.identitymanagement.response.AuthenticationResponse;
import org.ead.identitymanagement.response.AuthorizationResponse;
import org.ead.identitymanagement.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final WebClient.Builder webClientBuilder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest request) {
        User checkUser = repository.findByEmail(request.getEmail()).orElse(null);
        if(checkUser!=null){
            return "User exists";
        }
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(Role.USER);
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
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
                .uri("http://usermanagement/api/user/system")
                .bodyValue(createUserDTO)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.out.println(e.getMessage());
                    throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to user management");
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

    public String deleteUser(String email) {
        try {
            repository.deleteUserByEmail(email);
        }catch(Exception e){
            System.out.println(e.getMessage());
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
        return "Success";
    }

    public String assignRole(AssignRole request) {
        User user = repository.findByEmail(request.getEmail()).orElse(null);
        if(user==null){
            throw new RestException(HttpStatus.NOT_FOUND, "User not found");
        }
        Collection<Role> currentRoles = user.getRoles();
        if(request.getRole().equals("ADMIN")){
            currentRoles.add(Role.ADMIN);
        }
        else if(request.getRole().equals("DELIVERER")){
            currentRoles.add(Role.DELIVERER);
        }
        else{
            throw new RestException(HttpStatus.BAD_REQUEST, "Invalid role");
        }
        user.setRoles(currentRoles);
        repository.save(user);
        return "Success";
    }

    public String removeRole(AssignRole request) {
        User user = repository.findByEmail(request.getEmail()).orElse(null);
        if(user==null){
            throw new RestException(HttpStatus.NOT_FOUND, "User not found");
        }
        Collection<Role> currentRoles = user.getRoles();
        if(request.getRole().equals("ADMIN")){
            currentRoles.remove(Role.ADMIN);
        }
        else if(request.getRole().equals("DELIVERER")){
            currentRoles.remove(Role.DELIVERER);
        }
        else{
            throw new RestException(HttpStatus.BAD_REQUEST, "Invalid role");
        }
        user.setRoles(currentRoles);
        repository.save(user);
        return "Success";
    }

    public String changePassword(ChangePasswordDTO request, String email) {
        User user = repository.findByEmail(email).orElse(null);
        if(user==null){
            throw new RestException(HttpStatus.NOT_FOUND, "User not found");
        }
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            throw new RestException(HttpStatus.BAD_REQUEST, "Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
        return "Success";
    }

    public AuthorizationResponse authorization(String header){
        String authHeader = header;
        if (authHeader!=null && authHeader.startsWith("Bearer ")){
            authHeader = authHeader.substring(7);
            System.out.println(authHeader);
            try {
                jwtUtil.validateToken(authHeader);
            }catch (Exception e){
                throw new RestException(HttpStatus.UNAUTHORIZED,"Invalid Token");
            }
        } else {
            throw new RestException(HttpStatus.UNAUTHORIZED,"Header not found");
        }

        String username = jwtUtil.extractUsername(authHeader);
        User user;
        try {
            user = repository.findByEmail(username).orElseThrow();
        }catch (Exception e){
            throw new RestException(HttpStatus.NOT_FOUND,"User not found");
        }

        AuthorizationResponse authorizationResponse = AuthorizationResponse.builder()
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();

        return authorizationResponse;
    }

}
