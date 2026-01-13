package com.vozhe.jwt.controller;

import com.vozhe.jwt.models.User;
import com.vozhe.jwt.payload.request.JwtTokenRequest;
import com.vozhe.jwt.payload.response.JwtTokenResponse;
import com.vozhe.jwt.payload.response.UserDTO;
import com.vozhe.jwt.service.AccountCreationService;
import com.vozhe.jwt.service.UserService;
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

    @PostMapping("authenticate")
    public ResponseEntity<JwtTokenResponse> createAuthenticationToken(@RequestBody JwtTokenRequest authenticationRequest) throws Exception {
        return accountCreationService.createToken(authenticationRequest);
    }

    @GetMapping("user/info")
    public ResponseEntity<UserDTO> getUserInfo(Principal user){
        return  accountCreationService.getUserInfo(user);
    }



}
