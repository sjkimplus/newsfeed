package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.alarm.Alarm;
import com.sparta.newsfeed.entity.alarm.AlarmTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByUserIdOrderByIdDesc(Long userId); // userId에 해당하는 List<Alarm> alarmId 내림차순

    Alarm findByTypeAndItemId(AlarmTypeEnum type, Long itemId);
}
