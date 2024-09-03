package com.sparta.newsfeed.dto.alarm;

import com.sparta.newsfeed.entity.alarm.Alarm;
import com.sparta.newsfeed.entity.alarm.AlarmTypeEnum;
import lombok.Getter;

@Getter
public class AlarmResponseDto {
    private final Long id;
    private final AlarmTypeEnum type;
    private final Long itemId;
    private final Long userId;


    public AlarmResponseDto(Alarm alarm) {
        this.id = alarm.getId();
        this.type = alarm.getType();
        this.itemId = alarm.getItemId();
        this.userId = alarm.getUser().getId();
    }
}
