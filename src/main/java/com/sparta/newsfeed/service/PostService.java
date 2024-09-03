package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.entity.*;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sparta.newsfeed.entity.Type.POST;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final PostCommentRepository postCommentRepository;

    public void createPost(long userId, PostRequestDto requestDto) {
        // find user
        User user = userRepository.findById(userId).orElseThrow();

        // make post
        Post post = new Post(user, requestDto.getContent());
        postRepository.save(post);

        // insert image
        Image img = new Image(post.getId(), Type.POST, requestDto.getImageUrl());
        imageRepository.save(img);
    }

//    public PostResponseDto getPost(long postId) {
//        // find the post
//        Post post = postRepository.findById(postId).orElseThrow();
//        // get the corresponding image of the post
//        Image image = imageRepository.findByTypeAndItemId(Type.POST, post.getId()).orElseThrow();
//        return new PostResponseDto(post, image.getImageUrl());
//    }

    public PostResponseDto getPost(long postId) {
        // find the post
        Post post = postRepository.findById(postId).orElseThrow();

        // get the corresponding image of the post
        Image image = imageRepository.findByTypeAndItemId(Type.POST, post.getId()).orElseThrow();

        // Get the number of likes
        Long likeCount = likeRepository.countByTypeAndItemId(LikeTypeEnum.POST, postId);

        // Get the comments
        List<PostComment> comments = postCommentRepository.findAllByPostId(postId);

        return new PostResponseDto(post, image.getImageUrl(), likeCount, comments);
    }
}