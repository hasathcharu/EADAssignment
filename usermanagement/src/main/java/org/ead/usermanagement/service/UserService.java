package org.ead.usermanagement.service;

import lombok.RequiredArgsConstructor;
import org.ead.usermanagement.dto.*;
import org.ead.usermanagement.exception.RestException;
import org.ead.usermanagement.model.User;
import org.ead.usermanagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final WebClient.Builder webClientBuilder;
    public void createUser(NewUserDTO newUserDTO) {

        User checkUser = userRepository.findByEmail(newUserDTO.getEmail()).orElse(null);
        if(checkUser!=null){
            throw new RestException(HttpStatus.CONFLICT, "User exists");
        }
        User user = User.builder()
                .name(newUserDTO.getName())
                .email(newUserDTO.getEmail())
                .gender(newUserDTO.getGender())
                .telephone(newUserDTO.getTelephone())
                .address(newUserDTO.getAddress())
                .build();

        userRepository.save(user);
    }

    public List<UsersResponse> getAllUsers() {
        List<User>users=userRepository.findAll();

        return users.stream().map(this::mapToUsersResponse).toList();
    }

    private UsersResponse mapToUsersResponse(User user) {
        return UsersResponse.builder()
                .id(user.getId().toString())
                .name(user.getName())
                .address(user.getAddress())
                .email(user.getEmail())
                .gender(user.getGender())
                .telephone(user.getTelephone())
                .build();
    }


    public UserDetailsDTO getUserDetails(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user==null){
            throw new RestException(HttpStatus.NOT_FOUND, "User not found");
        }
            return mapToUserDetailsDTO(user);
    }


    private UserDetailsDTO mapToUserDetailsDTO(User user){
        return UserDetailsDTO.builder()
                .name(user.getName())
                .address(user.getAddress())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .build();
    }

    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user==null){
            throw new RestException(HttpStatus.NOT_FOUND, "User not found");
        }

        String res = webClientBuilder.build()
                .delete()
                .uri("http://identitymanagement/api/auth/system/"+email)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.out.println(e.getMessage());
                    throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to identity management");
                })
                .block();
        if(Objects.equals(res, "Success")){
            userRepository.deleteUserByEmail(email);
            return;
        }
        throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");

    }


    public void updateUser(UpdateUserBasicDTO updateUserDTO, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user==null){
            throw new RestException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.setName(updateUserDTO.getName() !=null ? updateUserDTO.getName():user.getName());
//        user.setEmail(updateUserDTO.getEmail() !=null ? updateUserDTO.getEmail():user.getEmail());
        user.setAddress(updateUserDTO.getAddress() !=null ? updateUserDTO.getAddress():user.getAddress());
        user.setTelephone(updateUserDTO.getTelephone() !=null ? updateUserDTO.getTelephone():user.getTelephone());
        user.setGender(updateUserDTO.getGender() !=null ? updateUserDTO.getGender():user.getGender());
        userRepository.save(user);
    }
}
