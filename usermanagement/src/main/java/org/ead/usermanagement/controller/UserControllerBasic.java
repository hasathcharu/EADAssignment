package org.ead.usermanagement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.usermanagement.dto.*;
import org.ead.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/basic")
@RequiredArgsConstructor
public class UserControllerBasic {


    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDetailsDTO getUser(){
        return userService.getUserDetails("test@hasathcharu.com");
    }


    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(){
        userService.deleteUser("test@hasathcharu.com");
        return "Success";
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public String updateUser(@RequestBody UpdateUserBasicDTO updateUserDTO){
        userService.updateUser(updateUserDTO, "test@hasathcharu.com");
        return "Success";
    }
}
