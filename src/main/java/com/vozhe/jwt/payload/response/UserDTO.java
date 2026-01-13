package com.vozhe.jwt.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String username;
    private String authorities;
}
