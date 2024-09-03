package com.sparta.newsfeed.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "post")
@Getter
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}