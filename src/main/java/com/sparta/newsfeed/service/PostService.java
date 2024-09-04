package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.entity.*;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.entity.post.Post;
import com.sparta.newsfeed.entity.post.PostSortTypeEnum;
import com.sparta.newsfeed.repository.*;
import com.sparta.newsfeed.utile.FileUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sparta.newsfeed.entity.Type.POST;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final PostCommentRepository postCommentRepository;
    private final FileUtils fileUtils;

    public PostResponseDto createPost(String userEmail, PostRequestDto requestDto, List<MultipartFile> multipartFiles) throws Exception {
        // 사용자 찾기
        User user = userRepository.findByEmail(userEmail).orElseThrow();

        // 게시물 생성
        Post post = new Post(user, requestDto.getContent());
        postRepository.save(post);

        // 파일 저장 및 이미지 URL 리스트 생성
        List<String> imagePaths = fileUtils.parseInsertFileInfo(multipartFiles, POST);

        for (String imagePath : imagePaths) {
            // 이미지 URL을 DB에 저장
            if (!imagePath.isEmpty()) {
                Image img = new Image(post.getId(), Type.POST, imagePath);
                imageRepository.save(img);
            }
        }
        return getPost(post.getId());  // 이미지 URL 리스트 반환
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

//    public Page<PostResponseDto> getPosts(long postId, PostSortTypeEnum type, int page, int size) {
//
//        Sort.Direction direction = Sort.Direction.DESC;
//        Sort sort  = Sort.by(direction, "modifiedDate");
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<Schedule> schedules = scheduleRepository.findAll(pageable);
//        return schedules.map(schedule -> new GeneralScheduleResponseDto(schedule));
//
//        return null;
//    }
}