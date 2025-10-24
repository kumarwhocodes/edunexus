package dev.kumar.edunexus.controller;

import dev.kumar.edunexus.dto.AccessTokenBody;
import dev.kumar.edunexus.dto.CustomResponse;
import dev.kumar.edunexus.dto.UserDTO;
import dev.kumar.edunexus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService service;
    
    @PostMapping("/login")
    public CustomResponse<UserDTO> loginUserHandler(@RequestBody AccessTokenBody tokenBody) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "User Logged Successfully",
                service.loginUser(tokenBody)
        );
    }
    
    @GetMapping("")
    public CustomResponse<UserDTO> fetchUserHandler(
            @RequestHeader("Authorization") String token
    ) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "User Fetched Successfully.",
                service.fetchUser(token)
        );
    }
    
    @PutMapping("")
    public CustomResponse<UserDTO> updateUserHandler(
            @RequestHeader("Authorization") String token,
            @RequestBody UserDTO userDTO
    ) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "User Updated Successfully",
                service.updateUser(token, userDTO)
        );
    }
    
    @DeleteMapping("")
    public CustomResponse<Void> deleteUserHandler(
            @RequestHeader("Authorization") String token
    ) {
        service.deleteUser(token);
        return new CustomResponse<>(
                HttpStatus.OK,
                "User deleted successfully",
                null
        );
    }
    
}
