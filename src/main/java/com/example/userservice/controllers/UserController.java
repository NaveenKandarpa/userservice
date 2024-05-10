package com.example.userservice.controllers;

import com.example.userservice.dtos.LoginRequestDto;
import com.example.userservice.dtos.SignUpRequestDto;
import com.example.userservice.dtos.UserDto;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto requestDto) {
        User user = userService.signUp(requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword());
        UserDto userDto = UserDto.from(user);
        return userDto;
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDto dto) {
        return userService.login(dto.getEmail(), dto.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LoginRequestDto dto) {
        return null;
    }

    @PostMapping({"validate/{token}"})
    public UserDto validateToken(@PathVariable String token) {
        return null;
    }

    @GetMapping("/users/{id}")
    public UserDto getUserById(@PathVariable Long id){
        return null;
    }
}
