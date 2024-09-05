package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.entity.*;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.repository.*;
import com.sparta.newsfeed.utile.FileUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
//        List<String> imagePaths = fileUtils.parseInsertFileInfo(multipartFiles, POST);
//
//        for (String imagePath : imagePaths) {
//            // 이미지 URL을 DB에 저장
//            if (!imagePath.isEmpty()) {
//                Image img = new Image(post.getId(), Type.POST, imagePath);
//                imageRepository.save(img);
//            }
//        }
        fileUtils.saveImage(POST, multipartFiles, post.getId());

        return getPost(post.getId());  // 이미지 URL 리스트 반환
    }

    public PostResponseDto getPost(long postId) {
        // find the post
        Post post = postRepository.findById(postId).orElseThrow();


        List<String> imageUrls = fileUtils.getImage(POST,post.getId());

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

    @Transactional
    public void deletePost(long postId) throws IOException {
        postRepository.deleteById(postId);

        // 1. 기존 이미지 파일을 가져오기
        List<Image> imagesToDelete = imageRepository.findByItemId(postId);

        // 1.1 기존 파일 삭제 (DB 및 파일 시스템)
        fileUtils.deleteExistingImages(imagesToDelete);

    }
}