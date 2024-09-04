package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.entity.*;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public void createPost(long userId, PostRequestDto requestDto, List<MultipartFile> multipartFile) {
        // find user
        User user = userRepository.findById(userId).orElseThrow();

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

        // Create and return the PostResponseDto with the collected image URLs
        return new PostResponseDto(post, imageUrls);

    }
}