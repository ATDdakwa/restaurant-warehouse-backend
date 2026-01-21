package com.vozhe.jwt.controller;

import com.vozhe.jwt.models.User;
import com.vozhe.jwt.payload.BaseResult;
import com.vozhe.jwt.payload.request.JwtTokenRequest;
import com.vozhe.jwt.payload.response.JwtTokenResponse;
import com.vozhe.jwt.payload.response.UserDTO;
import com.vozhe.jwt.service.AccountCreationService;
import com.vozhe.jwt.service.UserService; // Ensure UserService is imported
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/whitelist/api/")

@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final AccountCreationService accountCreationService;
    private final UserService userService; // Inject UserService

    @PostMapping("authenticate")
    public ResponseEntity<JwtTokenResponse> createAuthenticationToken(@RequestBody JwtTokenRequest authenticationRequest) throws Exception {
        return accountCreationService.createToken(authenticationRequest);
    }

    @GetMapping("user/info")
    public ResponseEntity<UserDTO> getUserInfo(Principal user){
        return  accountCreationService.getUserInfo(user);
    }

    @GetMapping("users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("users/{id}")
    public ResponseEntity<BaseResult> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userService.updateUser(id, userDetails);
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<BaseResult> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
