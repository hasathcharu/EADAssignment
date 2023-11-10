package org.ead.identitymanagement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.identitymanagement.dto.ChangePasswordDTO;
import org.ead.identitymanagement.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/basic")
public class AuthenticationControllerBasic {

    private final AuthService service;

    @PutMapping("/change-password")
    public String changePassword(
            @RequestBody ChangePasswordDTO request,
            @RequestHeader("X-User-Email") String userEmail
    ){
        return service.changePassword(request, userEmail);
    }


}
