package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.entity.*;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final PostCommentRepository postCommentRepository;

    public void createPost(String userEmail, PostRequestDto requestDto, List<MultipartFile> multipartFile) {
        // find user
        User user = userRepository.findByEmail(userEmail).orElseThrow();

        // make post
        Post post = new Post(user, requestDto.getContent());
        postRepository.save(post);

        for (MultipartFile imgUrl : multipartFile){
            // insert image
            if (!imgUrl.isEmpty()) {
                Image img = new Image(post.getId(), Type.POST, imgUrl.getOriginalFilename());
                imageRepository.save(img);
            }
        }
    }

    public PostResponseDto getPost(long postId) {
        // find the post
        Post post = postRepository.findById(postId).orElseThrow();

        // Initialize a list to store image URLs
        List<String> imageUrls = new ArrayList<>();

        // get the corresponding images of the post
        List<Image> images = imageRepository.findAllByTypeAndItemId(Type.POST, post.getId());
        for (Image file : images) {
            imageUrls.addAll(file.getImageUrl()); // Add all image URLs to the list
        }

        // get the number of likes
        Long likeCount = likeRepository.countByTypeAndItemId(LikeTypeEnum.POST, postId);

        // get the comments
        List<PostComment> comments = postCommentRepository.findAllByPostId(postId);

        // Create and return the PostResponseDto with the collected image URLs
        return new PostResponseDto(post, imageUrls, likeCount, comments);
    }

    public void updatePost(long postId, PostRequestDto requestDto) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("게시물을 찾을 수 없습니다."));
        post.updatePost(requestDto.getContent());
    }

    public void deletePost(long postId) {
        postRepository.deleteById(postId);
    }
}