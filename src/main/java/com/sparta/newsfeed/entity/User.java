package com.sparta.newsfeed.entity;

import com.sparta.newsfeed.dto.user.UserRequestDto;
import com.sparta.newsfeed.dto.user.UserUpdateRequestDto;
import com.sparta.newsfeed.entity.relation.Relationship;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends Timestamped{

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

    @OneToMany(mappedBy = "sentUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Relationship> sentRelationshipList = new ArrayList<>();

    @OneToMany(mappedBy = "receivedUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Relationship> receivedRelationshipList = new ArrayList<>();

    public User(UserRequestDto userRequestDto, String password) {
        this.email = userRequestDto.getEmail();
        this.password = password;
        this.name = userRequestDto.getName();
        this.birthday = userRequestDto.getBirthday();
    }

    public void update(UserUpdateRequestDto userUpdateRequestDto){
        if(userUpdateRequestDto.getName() != null) this.name = userUpdateRequestDto.getName();
        if(userUpdateRequestDto.getBirthday() != null) this.birthday = userUpdateRequestDto.getBirthday();
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
    }

    public void deleteUpdate(LocalDateTime deleteTime){
        this.dateDeleted = deleteTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", dateDeleted=" + dateDeleted +
                ", sentRelationshipList=" + sentRelationshipList +
                ", receivedRelationshipList=" + receivedRelationshipList +
                '}';
    }
}
