package com.vozhe.jwt.service;

import com.vozhe.jwt.payload.request.JwtTokenRequest;
import com.vozhe.jwt.payload.response.JwtTokenResponse;
import com.vozhe.jwt.payload.response.UserDTO;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface AccountCreationService {
    ResponseEntity<JwtTokenResponse> createToken(JwtTokenRequest authenticationRequest) throws Exception;
    ResponseEntity<UserDTO> getUserInfo(Principal user);
}
