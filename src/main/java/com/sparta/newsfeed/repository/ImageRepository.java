package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.Image;
import com.sparta.newsfeed.entity.Type;
import com.sparta.newsfeed.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByTypeAndItemId (Type type, Long itemId);


    @Query("SELECT i FROM Image i WHERE i.itemId = :itemId")
    List<Image> findByItemId(@Param("itemId") Long itemId);

    Image findByIdAndType(Long id, Type user);

    List<Image> findByTypeAndItemId(Type user, Long id);
}
