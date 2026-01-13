package com.vozhe.jwt.service;

import com.vozhe.jwt.models.User;
import com.vozhe.jwt.models.UserRole;
import com.vozhe.jwt.payload.BaseResult;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    ResponseEntity<BaseResult> createUser(User user);
    public String getCurrentUsername();
    public User getCurrentUser();


}
