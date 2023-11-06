package org.ead.usermanagement.controller;


import lombok.RequiredArgsConstructor;
import org.ead.usermanagement.dto.UserDetailsDTO;
import org.ead.usermanagement.dto.UsersResponse;
import org.ead.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/admin")
@RequiredArgsConstructor
public class UserControllerAdmin {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UsersResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDetailsDTO getUser(@PathVariable String email){
        return userService.getUserDetails(email);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(@PathVariable String email){
        userService.deleteUser(email);
        return "Success";
    }
}
