package com.sparta.newsfeed.entity.alarm;

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
    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AlarmTypeEnum type;
    @Column(name = "itemId", nullable = false)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Alarm(AlarmTypeEnum type, Long itemId, User user) {
        this.type = type;
        this.itemId = itemId;
        this.user = user;
    }
}
