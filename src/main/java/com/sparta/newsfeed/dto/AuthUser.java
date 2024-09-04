package com.sparta.newsfeed.dto;

import lombok.Getter;

@Getter
public class AuthUser {
    private final String email;

    public AuthUser(String userEmail) {
        this.email = userEmail;
    }
}
