package org.ead.identitymanagement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.identitymanagement.dto.AssignRole;
import org.ead.identitymanagement.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/admin")
public class AuthenticationControllerAdmin {

    private final AuthService service;

    @PutMapping("/assign-role")
    public String assignRole(
            @RequestBody AssignRole request
    ){
        return service.assignRole(request);
    }
    @PutMapping("/remove-role")
    public String removeRole(
            @RequestBody AssignRole request
    ){
        return service.removeRole(request);
    }


}
