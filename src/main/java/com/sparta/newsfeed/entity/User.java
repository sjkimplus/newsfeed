package com.sparta.newsfeed.entity;

import com.sparta.newsfeed.dto.user.UserRequestDto;
import com.sparta.newsfeed.dto.user.UserUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birthday", nullable = false)
    private String birthday;

    @Column(name = "date_deleted")
    private LocalDateTime dateDeleted;

    public User(UserRequestDto userRequestDto, String password) {
        this.email = userRequestDto.getEmail();
        this.password = password;
        this.name = userRequestDto.getName();
        this.birthday = userRequestDto.getBirthday();
    }

    public void update(UserUpdateRequestDto userUpdateRequestDto){
        if(userUpdateRequestDto.getName() != null) this.name = userUpdateRequestDto.getName();
        if(userUpdateRequestDto.getBirthday() != null) this.name = userUpdateRequestDto.getBirthday();
    }

    public void updatePassword(UserUpdateRequestDto userUpdateRequestDto){
        this.password = userUpdateRequestDto.getNewPassword();
    }

    public void deleteUpdate(LocalDateTime deleteTime){
        this.dateDeleted = deleteTime;
    }

}
