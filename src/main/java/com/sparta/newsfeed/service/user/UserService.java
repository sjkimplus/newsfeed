package com.sparta.newsfeed.service.user;

import com.sparta.newsfeed.config.PasswordEncoder;
import com.sparta.newsfeed.dto.user.LoginRequestDto;
import com.sparta.newsfeed.dto.user.UserRequestDto;
import com.sparta.newsfeed.dto.user.UserResponseDto;
import com.sparta.newsfeed.dto.user.UserUpdateRequestDto;
import com.sparta.newsfeed.entity.Image;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.exception.DataDuplicationException;
import com.sparta.newsfeed.exception.DataNotFoundException;
import com.sparta.newsfeed.exception.PasswordMismatchException;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.repository.ImageRepository;
import com.sparta.newsfeed.repository.RelationshipRepository;
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
    private final RelationshipRepository relationshipRepository;
    private final PasswordEncoder passwordEncoder;

    private final ImageRepository imageRepository;
    private final FileUtils fileUtils;

    public UserResponseDto create(UserRequestDto userRequestDto) {
        String password = passwordEncoder.encode(userRequestDto.getPassword());
        String email = userRequestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) throw new DataDuplicationException("중복된 아이디 입니다.");

        User user = new User(userRequestDto, password);
        userRepository.save(user);

        return new UserResponseDto(user);
    }

    public UserResponseDto login(JwtUtil jwtUtil, LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new DataNotFoundException("해당 사용자가 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException(email + " 의 " + "패스워드가 올바르지 않습니다.");
        }

        List<String> imageUrls = fileUtils.getImage(USER,user.getId());

        String token = jwtUtil.createToken(user.getEmail());
        jwtUtil.addJwtToCookie(token, httpServletResponse);

        return new UserResponseDto(user, imageUrls);
    }

    @Transactional
    public UserResponseDto update(String email, UserUpdateRequestDto userUpdateRequestDto) {
        User user = findUser(email);

        if (userUpdateRequestDto.getNewPassword() != null) {
            if (!passwordEncoder.matches(userUpdateRequestDto.getCurrentPassword(), user.getPassword())) {
                throw new PasswordMismatchException(email + " 의 " + "패스워드가 올바르지 않습니다.");
            }
            if (user.getPassword().equals(userUpdateRequestDto.getNewPassword())) {
                throw new DataDuplicationException("이전과 동일한 비밀번호 입니다. 새롭게 지정해주세요");
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
            throw new PasswordMismatchException(loginRequestDto.getEmail() + " 의 " + "패스워드가 올바르지 않습니다.");
        }

        user.deleteUpdate(java.time.LocalDateTime.now());

        //실제 삭제가 아닌 소프트 삭제여서 Cascade 안통함. 그래서 수동으로 연관 데이터 전부 삭제
        relationshipRepository.deleteBySentUserIdOrReceivedUserId(user.getId(), user.getId());

        return "삭제 완료";
    }
    public User findUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new DataNotFoundException("선택한 유저는 존재하지 않습니다."));
        if (user.getDateDeleted() != null) {
            throw new DataNotFoundException("이미 삭제된 유저 입니다");
        }

        return user;
    }

    public UserResponseDto findProfile(String email) {
        User user = findUser(email);

        List<String> imageUrls = fileUtils.getImage(USER, user.getId());

        return new UserResponseDto(user, imageUrls);
    }

    @Transactional
    public List<String> createUsersImage(String email, List<MultipartFile> multipartFile) throws IOException {
        return fileUtils.createUsersImage(email, multipartFile);
    }

    @Transactional
    public List<String> modifyUsersImage(String email, List<MultipartFile> multipartFile) throws IOException {
       return fileUtils.modifyUsersImage(email, multipartFile);
    }

}
