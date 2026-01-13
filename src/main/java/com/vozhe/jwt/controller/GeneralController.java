package com.vozhe.jwt.controller;

import com.vozhe.jwt.models.User;
import com.vozhe.jwt.payload.BaseResult;
import com.vozhe.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Create user

@RestController
@RequestMapping("/whitelist/api/")
@RequiredArgsConstructor
public class GeneralController {
    private final UserService userService;

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("create-user")
    public ResponseEntity<BaseResult> createUser(@RequestBody User user){
        return userService.createUser(user);
    }

}
