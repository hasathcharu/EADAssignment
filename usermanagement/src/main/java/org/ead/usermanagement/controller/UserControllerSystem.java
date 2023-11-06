package org.ead.usermanagement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.usermanagement.dto.NewUserDTO;
import org.ead.usermanagement.dto.UserDetailsDTO;
import org.ead.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/system")
@RequiredArgsConstructor
public class UserControllerSystem {

    private final UserService userService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createUser(@RequestBody NewUserDTO newUserDTO){
        userService.createUser(newUserDTO);
        return "Success";
    }
    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDetailsDTO getUser(@PathVariable String email){
        return userService.getUserDetails(email);
    }
}
