package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.entity.Image;
import com.sparta.newsfeed.entity.Post;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.repository.ImageRepository;
import com.sparta.newsfeed.repository.PostRepository;
import com.sparta.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sparta.newsfeed.entity.Type.POST;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public void createPost(long userId, PostRequestDto requestDto) {
        // find user
        User user = userRepository.findById(userId).orElseThrow();

        // make post
        Post post = new Post(user, requestDto.getContent());
        postRepository.save(post);

        // insert image
        Image img = new Image(post.getId(), POST, requestDto.getImageUrl());
        imageRepository.save(img);
    }

    public PostResponseDto getPost(long postId) {
        // find the post
        Post post = postRepository.findById(postId).orElseThrow();
        // get the corresponding image of the post
        Image image = imageRepository.findByTypeAndItemId(POST, post.getId()).orElseThrow();
        return new PostResponseDto(post, image.getImageUrl());
    }
}