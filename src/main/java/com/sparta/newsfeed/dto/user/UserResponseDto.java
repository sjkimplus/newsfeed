package com.sparta.newsfeed.dto.user;

import com.sparta.newsfeed.entity.User;
import lombok.Getter;

import java.util.List;

@Getter
public class UserResponseDto {

    private String name;
    private String email;
    private String birthday;

    private List<String> imageUrl;

    public UserResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.birthday = user.getBirthday();
    }

    public UserResponseDto(User user, List<String> imageUrl) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.birthday = user.getBirthday();
        this.imageUrl = imageUrl;
    }
}
