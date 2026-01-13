package com.vozhe.jwt.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JwtTokenResponse implements Serializable {

    private String accessToken;
    private String tokenType = "Bearer";
    private String code;
    private UserDTO user;

    public JwtTokenResponse() {
    }

    public JwtTokenResponse(String accessToken, UserDTO user, String code) {
        this.accessToken = accessToken;
        this.user = user;
        this.code = code;
    }
}
