package com.sparta.newsfeed.entity.alarm;

import com.sparta.newsfeed.dto.alarm.AlarmRequestDto;
import com.sparta.newsfeed.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "alarm")
@NoArgsConstructor
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @Enumerated(value = EnumType.STRING)
    private AlarmTypeEnum type;
    @Column
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Alarm(AlarmRequestDto requestDto, User user) {
        if (requestDto.getType()) {
            this.type = AlarmTypeEnum.COMMENT;
        } else {
            this.type = AlarmTypeEnum.LIKE;
        }
        this.itemId = requestDto.getItemId();
        this.user = user;
    }
}
