package org.ead.identitymanagement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.identitymanagement.service.AuthService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/system")
public class AuthenticationControllerSystem {

    private final AuthService service;
    @DeleteMapping("/{email}")
    public String deleteUser(@PathVariable String email){
        System.out.println("Hello auth");
        return service.deleteUser(email);
    }
}
