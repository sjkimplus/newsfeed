package com.sparta.newsfeed.service.user;

import com.sparta.newsfeed.config.PasswordEncoder;
import com.sparta.newsfeed.dto.user.LoginRequestDto;
import com.sparta.newsfeed.dto.user.UserRequestDto;
import com.sparta.newsfeed.dto.user.UserResponseDto;
import com.sparta.newsfeed.dto.user.UserUpdateRequestDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.PatternMatchUtils;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String passwordPrefix = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public UserResponseDto create(UserRequestDto userRequestDto){
        String password = passwordEncoder.encode(userRequestDto.getPassword());
        String email = userRequestDto.getEmail();;
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if(checkEmail.isPresent()) throw new IllegalArgumentException("중복된 아이디 입니다.");

        User user = new User(userRequestDto, password);
        userRepository.save(user);

        return new UserResponseDto(user);
    }

    public UserResponseDto login(JwtUtil jwtUtil, LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse){
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        User user =  userRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("해당 사용자가 없습니다."));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createToken(user.getEmail());
        jwtUtil.addJwtToCookie(token, httpServletResponse);

        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto update(String email, UserUpdateRequestDto userUpdateRequestDto){
        User user = findUser(email);

        if(userUpdateRequestDto.getNewPassword() != null){
            if(!passwordEncoder.matches(userUpdateRequestDto.getCurrentPassword(), user.getPassword())){
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            user.updatePassword(userUpdateRequestDto);
        }
        user.update(userUpdateRequestDto);
        return new UserResponseDto(user);
    }

    @Transactional
    public String delete(LoginRequestDto loginRequestDto){
        User user = findUser(loginRequestDto.getEmail());

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        user.deleteUpdate(java.time.LocalDateTime.now());

        return "삭제 완료";
    }

    public User findUser(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("선택한 유저는 존재하지 않습니다."));
        if(user.getDateDeleted() != null){
            throw new IllegalArgumentException("이미 삭제된 유저 입니다");
        }

        return user;
    }

    public UserResponseDto findProfile(String email){
        User user = findUser(email);

        return new UserResponseDto(user);
    }
}
