package org.ead.usermanagement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.usermanagement.dto.*;
import org.ead.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createUser(@RequestBody NewUserDTO newUserDTO){
        userService.createUser(newUserDTO);
        return "Success";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UsersResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDetailsDTO getUserDetails(@PathVariable String email){
        return userService.getUserDetails(email);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(@PathVariable String email){
        userService.deleteUser(email);
        return "Success";
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public String updateUser(@RequestBody UpdateUserDTO updateUserDTO){
        userService.updateUser(updateUserDTO);
        return "Success";
    }
}
