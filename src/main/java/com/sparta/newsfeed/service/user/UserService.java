package com.sparta.newsfeed.service.user;

import com.sparta.newsfeed.config.PasswordEncoder;
import com.sparta.newsfeed.dto.user.LoginRequestDto;
import com.sparta.newsfeed.dto.user.UserRequestDto;
import com.sparta.newsfeed.dto.user.UserResponseDto;
import com.sparta.newsfeed.dto.user.UserUpdateRequestDto;
import com.sparta.newsfeed.entity.Image;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.repository.ImageRepository;
import com.sparta.newsfeed.repository.UserRepository;
import com.sparta.newsfeed.utile.FileUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sparta.newsfeed.entity.Type.USER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final ImageRepository imageRepository;
    private final FileUtils fileUtils;
    @Value("${file.upload.path}")
    private String filePath;

    public UserResponseDto create(UserRequestDto userRequestDto) {
        String password = passwordEncoder.encode(userRequestDto.getPassword());
        String email = userRequestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) throw new IllegalArgumentException("중복된 아이디 입니다.");

        User user = new User(userRequestDto, password);
        userRepository.save(user);

        return new UserResponseDto(user);
    }

    public UserResponseDto login(JwtUtil jwtUtil, LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("해당 사용자가 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        List<String> saveUserImage = new ArrayList<>();
        List<Image> byTypeAndItemId = imageRepository.findByTypeAndItemId(USER, user.getId());
        for (Image image : byTypeAndItemId) {
            saveUserImage.addAll(image.getImageUrl());
        }

        String token = jwtUtil.createToken(user.getEmail());
        jwtUtil.addJwtToCookie(token, httpServletResponse);

        return new UserResponseDto(user, saveUserImage);
    }

    @Transactional
    public UserResponseDto update(String email, UserUpdateRequestDto userUpdateRequestDto) {
        User user = findUser(email);

        if (userUpdateRequestDto.getNewPassword() != null) {
            if (!passwordEncoder.matches(userUpdateRequestDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            if (user.getPassword().equals(userUpdateRequestDto.getNewPassword())) {
                throw new IllegalArgumentException("이전과 동일한 비밀번호 입니다. 새롭게 지정해주세요");
            }
            user.updatePassword(userUpdateRequestDto);
        }
        user.update(userUpdateRequestDto);
        return new UserResponseDto(user);
    }

    @Transactional
    public String delete(LoginRequestDto loginRequestDto) {
        User user = findUser(loginRequestDto.getEmail());

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        user.deleteUpdate(java.time.LocalDateTime.now());

        return "삭제 완료";
    }
    public User findUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("선택한 유저는 존재하지 않습니다."));
        if (user.getDateDeleted() != null) {
            throw new IllegalArgumentException("이미 삭제된 유저 입니다");
        }

        return user;
    }

    public UserResponseDto findProfile(String email) {
        User user = findUser(email);

        // Initialize a list to store image URLs
        List<String> imageUrls = new ArrayList<>();

        // get the corresponding images of the post
        List<Image> images = imageRepository.findAllByTypeAndItemId(USER, user.getId());
        for (Image file : images) {
            imageUrls.addAll(file.getImageUrl()); // Add all image URLs to the list
        }

        return new UserResponseDto(user, imageUrls);
    }

    @Transactional
    public List<String> createUsersImage(String email, List<MultipartFile> multipartFile) throws IOException {
        User user = findUser(email);
        List<Image> allByTypeAndItemId = imageRepository.findAllByTypeAndItemId(USER, user.getId());

        if (allByTypeAndItemId.size() > 0) {
            throw new IllegalStateException("이미 저장된 사진이 있습니다.");  // 예외 처리로 반환
        }

        if (multipartFile.size() == 1) {
            List<String> imagePaths = fileUtils.parseInsertFileInfo(multipartFile, USER);

            for (String imagePath : imagePaths) {
                // 이미지 URL을 DB에 저장
                if (!imagePath.isEmpty()) {
                    Image img = new Image(user.getId(), USER, imagePath);
                    imageRepository.save(img);
                }
            }
            return imagePaths;  // 이미지 URL 리스트 반환
        }
        throw new IllegalArgumentException("");
    }

    @Transactional
    public List<String> modifyUsersImage(String email, List<MultipartFile> multipartFile) throws IOException {
        User user = findUser(email);

        // 1. 기존 이미지 파일을 가져오기
        List<Image> imagesToDelete = imageRepository.findByItemId(user.getId());

        // multipartFile이 null이 아니고 비어있지 않은 경우
        if (multipartFile != null && !multipartFile.isEmpty()) {

            // 1.1 기존 파일 삭제 (DB 및 파일 시스템)
            deleteExistingImages(imagesToDelete);

            // 1.2 새로운 이미지 파일 저장
            List<String> newImagePaths = fileUtils.parseInsertFileInfo(multipartFile, USER);

            // 1.3 새 이미지 경로를 DB에 저장
            for (String imagePath : newImagePaths) {
                Image newImage = new Image(user.getId(), USER, imagePath);
                imageRepository.save(newImage);  // 새 이미지 저장
            }

            return newImagePaths;  // 업데이트된 이미지 리스트 반환
        } else {
            // 2. 새로운 이미지가 없는 경우, 기존 파일만 삭제
            deleteExistingImages(imagesToDelete);
        }

        return new ArrayList<>();  // 빈 리스트 반환
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
