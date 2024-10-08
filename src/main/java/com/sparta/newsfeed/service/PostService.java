package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.post.PageResponseDto;
import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.entity.*;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.entity.post.Post;
import com.sparta.newsfeed.entity.post.PostSortTypeEnum;
import com.sparta.newsfeed.exception.DataNotFoundException;
import com.sparta.newsfeed.repository.*;
import com.sparta.newsfeed.service.user.RelationshipService;
import com.sparta.newsfeed.utile.FileUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sparta.newsfeed.entity.Type.POST;
import static com.sparta.newsfeed.entity.post.PostSortTypeEnum.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final RelationshipService relationshipService;
    private final PostCommentRepository postCommentRepository;
    private final FileUtils fileUtils;


    public PostResponseDto createPost(String userEmail, PostRequestDto requestDto, List<MultipartFile> multipartFiles) throws Exception {
        // 사용자 찾기
        User user = userRepository.findByEmail(userEmail).orElseThrow();

        // 게시물 생성
        Post post = new Post(user, requestDto.getContent());
        postRepository.save(post);

        fileUtils.saveImage(POST, multipartFiles, post.getId());

        return getPost(post.getId());  // 이미지 URL 리스트 반환
    }

    public PostResponseDto getPost(long postId) {
        // find the post
        Post post = postRepository.findById(postId).orElseThrow(() -> new DataNotFoundException("id " + postId + "번 게시물은 존제하지 않습니다."));


        List<String> imageUrls = fileUtils.getImage(POST,post.getId());

        // get the number of likes
        Long likeCount = likeRepository.countByTypeAndItemId(LikeTypeEnum.POST, postId);

        // get the comments
        List<PostComment> comments = postCommentRepository.findAllByPostId(postId);

        // Create and return the PostResponseDto with the collected image URLs
        return new PostResponseDto(post, imageUrls, likeCount, comments);
    }

    @Transactional
    public void updatePost(long postId, PostRequestDto requestDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new DataNotFoundException("ID " + postId + "번 게시물은 존제하지 않습니다."));
        post.updatePost(requestDto.getContent());
    }

    @Transactional
    public void deletePost(long postId) throws IOException {
        postRepository.deleteById(postId);

        // 1. 기존 이미지 파일을 가져오기
        List<Image> imagesToDelete = imageRepository.findByItemId(postId);

        // 1.1 기존 파일 삭제 (DB 및 파일 시스템)
        fileUtils.deleteExistingImages(imagesToDelete);
    }

    public Page<PageResponseDto> getPosts(String feedUserEmail, int page, int size) {

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort  = Sort.by(direction, "createdDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts = postRepository.findAllByUserEmail(feedUserEmail, pageable);

        // Map each Post to PageResponseDto
        Page<PageResponseDto> responseDtos = posts.map(post -> {
            // Fetch image URLs for the post
            List<String> imageUrls = new ArrayList<>();
            List<Image> images = imageRepository.findAllByTypeAndItemId(POST, post.getId());
            for (Image file : images) {
                imageUrls.add(file.getImageUrl().toString()); // Add image URLs to the list
            }

            // Create PageResponseDto for each Post
            return new PageResponseDto(post, imageUrls);
        });

        return responseDtos;
    }


    public Page<PageResponseDto> getNewsFeed(String userEmail, PostSortTypeEnum type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Post> posts;
        if (type==RECENT) {
            posts = postRepository.findPostsByCreatedDateDesc();
        } else {
            posts = postRepository.findPostsByLikeCount();
        }

        // Map each Post to PageResponseDto
        List<PageResponseDto> dtoList = new ArrayList<>();
        for (Post post: posts)
        {
            if (relationshipService.checkFriend(userEmail, post.getUser().getEmail()) || userEmail.equals(post.getUser().getEmail())) {
                // Fetch image URLs for the post
                List<String> imageUrls = new ArrayList<>();
                List<Image> images = imageRepository.findAllByTypeAndItemId(POST, post.getId());
                for (Image file : images) {
                    imageUrls.add(file.getImageUrl().toString()); // Add image URLs to the list
                }
                // Create PageResponseDto for each Post
                PageResponseDto pageDto = new PageResponseDto(post, imageUrls);
                dtoList.add(pageDto);
            }
        }


        return new PageImpl<>(dtoList, pageable, dtoList.size());
    }

}