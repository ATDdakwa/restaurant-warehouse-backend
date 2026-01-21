package com.vozhe.jwt.service.Impl;

import com.vozhe.jwt.exceptions.UserExistsException;
import com.vozhe.jwt.models.User;
import com.vozhe.jwt.models.UserDetail;
import com.vozhe.jwt.payload.BaseResult;
import com.vozhe.jwt.repository.UserRepository;
import com.vozhe.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List; // Import List
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);
        user.orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
        return user.map(UserDetail::new).get();
    }

    //    Create User
    @Override
    public ResponseEntity<BaseResult> createUser(User user) {
        String name = user.getUsername();
        Optional<User> userCheck = userRepository.findUserByUsername(name);

        if(userCheck.isPresent()){
            throw new UserExistsException("User Already Exists " + name);
        }

        User toSaveUser = new User();
        toSaveUser.setFirstName(user.getFirstName());
        toSaveUser.setLastName(user.getLastName());
        toSaveUser.setUsername(user.getUsername());
        toSaveUser.setPhoneNumber(user.getPhoneNumber());
        toSaveUser.setPassword(encryptPassword(user.getPassword()));
        toSaveUser.setRoles(user.getRoles());
        userRepository.save(toSaveUser);

        return ResponseEntity.ok(new BaseResult("00", "User created Successfully", 200 ));

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public ResponseEntity<BaseResult> updateUser(Long id, User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }

        User existingUser = userOptional.get();
        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        existingUser.setIdNumber(userDetails.getIdNumber());
        existingUser.setAddress(userDetails.getAddress());
        existingUser.setDOB(userDetails.getDOB());
        existingUser.setRoles(userDetails.getRoles());

        // Only update password if a new one is provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(encryptPassword(userDetails.getPassword()));
        }

        userRepository.save(existingUser);
        return ResponseEntity.ok(new BaseResult("00", "User updated Successfully", 200));
    }

    @Override
    public ResponseEntity<BaseResult> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(new BaseResult("00", "User deleted Successfully", 200));
    }


    private String encryptPassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof String) {
            String principal = (String) authentication.getPrincipal();

            if (principal.compareTo("anonymousUser") != 0) {
                return null;
            }
            return principal;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null || username.isEmpty()) {
            throw new UserExistsException("Username cannot be empty");
        }
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UserExistsException("User not found");
        }
        return userOptional.get();
    }
}

