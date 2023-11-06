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
    public UserDetailsDTO getUser(@RequestHeader("X-User-Email") String userEmail){
        return userService.getUserDetails(userEmail);
    }


    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(@RequestHeader("X-User-Email") String userEmail){
        userService.deleteUser(userEmail);
        return "Success";
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public UserDetailsDTO updateUser(@RequestBody UpdateUserBasicDTO updateUserDTO, @RequestHeader("X-User-Email") String userEmail){
        return userService.updateUser(updateUserDTO, userEmail);

    }
}
