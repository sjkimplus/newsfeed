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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final String passwordPrefix = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private final FileUtils fileUtils;
    @Value("${file.upload.path}")
    private String filePath;
    public UserResponseDto create(UserRequestDto userRequestDto) {
        String password = passwordEncoder.encode(userRequestDto.getPassword());
        String email = userRequestDto.getEmail();
        ;
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
//        for (Image image : byTypeAndItemId) {
//            saveUserImage.addAll(image.getImageUrl());
//        }

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

        // multipartFile 자체가 null인 경우와 비어 있는지 확인
        if (multipartFile != null && !multipartFile.isEmpty()) {
            // 새로운 이미지가 전송된 경우
            List<Image> images = imageRepository.findByItemId(user.getId());
            List<String> list = fileUtils.parseInsertFileInfo(multipartFile, USER);

            // 이미지 리스트 순회하여 업데이트
            for (Image image : images) {
                File file =  new File(filePath + image.getImageUrl());
                System.out.println("file.getPath() = " + file.getPath());
                Image byIdAndType = imageRepository.findByIdAndType(image.getId(), USER);
                if (byIdAndType != null) {
                    byIdAndType.updateImageUrl(list); // 이미지 URL 업데이트
                }
            }

            return list;  // 업데이트된 이미지 리스트 반환
        } else if (multipartFile == null || multipartFile.isEmpty()) {
            // multipartFile이 없거나 빈 값일 경우, 이미지 삭제 처리
            List<Image> imagesToDelete = imageRepository.findByItemId(user.getId());
            for (Image image : imagesToDelete) {
                imageRepository.delete(image);
            }
        }

        return new ArrayList<>();  // 빈 리스트 반환
    }
}
