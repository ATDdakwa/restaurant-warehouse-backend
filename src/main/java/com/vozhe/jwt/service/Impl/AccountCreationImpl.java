package com.vozhe.jwt.service.Impl;


import com.vozhe.jwt.config.JwtTokenUtil;
import com.vozhe.jwt.models.User;
import com.vozhe.jwt.payload.request.JwtTokenRequest;
import com.vozhe.jwt.payload.response.JwtTokenResponse;
import com.vozhe.jwt.payload.response.UserDTO;
import com.vozhe.jwt.repository.UserRepository;
import com.vozhe.jwt.service.AccountCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountCreationImpl implements AccountCreationService {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserRepository userRepository;

    @Override
    public ResponseEntity<JwtTokenResponse> createToken(JwtTokenRequest authenticationRequest) throws Exception{

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        Optional<User> userResponse = userRepository.findUserByUsername(authenticationRequest.getUsername());
        if(!userResponse.isPresent()){
            userResponse.orElseThrow(() -> new UsernameNotFoundException("User not found" + authenticationRequest.getUsername()));
        }

        UserDTO user = new UserDTO();
        user.setUsername(userResponse.get().getUsername().toUpperCase());
        user.setAuthorities(userResponse.get().getRoles());
        user.setShopName(userResponse.get().getShopName());

        return ResponseEntity.ok(new JwtTokenResponse(token, user, "00"));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @Override
    public ResponseEntity<UserDTO> getUserInfo(Principal user) {
        Optional<User> userObject = userRepository.findUserByUsername(user.getName());
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userObject.get().getFirstName() + " " +  userObject.get().getFirstName());
        userDTO.setAuthorities(userObject.get().getRoles());
        return ResponseEntity.ok(userDTO);
    }
}
