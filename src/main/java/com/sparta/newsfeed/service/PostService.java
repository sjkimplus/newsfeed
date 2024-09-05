package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.entity.*;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.repository.*;
import com.sparta.newsfeed.utile.FileUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    @Value("${file.upload.path}")
    private String filePath;
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

    @Transactional
    public void deletePost(long postId) throws IOException {
        postRepository.deleteById(postId);

        // 1. 기존 이미지 파일을 가져오기
        List<Image> imagesToDelete = imageRepository.findByItemId(postId);

        // 1.1 기존 파일 삭제 (DB 및 파일 시스템)
        deleteExistingImages(imagesToDelete);

    }
    private void deleteExistingImages(List<Image> imagesToDelete) throws IOException {
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            for (Image image : imagesToDelete) {
                // 1. 데이터베이스에서 이미지 삭제
                imageRepository.delete(image);

                // 2. 파일 시스템에서 파일 삭제
                deleteImageFiles(image);
            }
        }
    }

    private void deleteImageFiles(Image image) {
        // 프로젝트 경로 가져오기
        String projectPath = System.getProperty("user.dir");

        // 이미지 URL 리스트 가져오기
        List<String> imageUrls = image.getImageUrl();

        for (String imageUrl : imageUrls) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // /files 경로를 제거하고 실제 파일 경로를 생성
                imageUrl = imageUrl.replace("/files", "");

                // 절대 경로 생성 (OS에 따라 파일 구분자를 일관되게 처리)
                String absoluteFilePath = projectPath + File.separator + filePath + imageUrl.replace("/", File.separator);
                System.out.println("삭제할 파일 경로: " + absoluteFilePath);

                // 파일 객체 생성 및 파일 삭제
                File fileToDelete = new File(absoluteFilePath);
                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        System.out.println("파일 삭제 성공: " + fileToDelete.getAbsolutePath());
                    } else {
                        System.out.println("파일 삭제 실패: " + fileToDelete.getAbsolutePath());
                    }
                } else {
                    System.out.println("파일이 존재하지 않습니다: " + fileToDelete.getAbsolutePath());
                }
            } else {
                System.out.println("이미지 URL이 null이거나 비어 있습니다.");
            }
        }
    }

}