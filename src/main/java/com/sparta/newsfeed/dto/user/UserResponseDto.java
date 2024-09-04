package com.sparta.newsfeed.dto.user;

import com.sparta.newsfeed.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private String name;
    private String email;
    private String birthday;

    public UserResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.birthday = user.getBirthday();
    }
}
