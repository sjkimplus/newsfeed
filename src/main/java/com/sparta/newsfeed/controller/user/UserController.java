package com.sparta.newsfeed.controller.user;

import com.sparta.newsfeed.dto.user.LoginRequestDto;
import com.sparta.newsfeed.dto.user.UserRequestDto;
import com.sparta.newsfeed.dto.user.UserResponseDto;
import com.sparta.newsfeed.dto.user.UserUpdateRequestDto;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/users/signup")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.create(userRequestDto));
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(userService.login(jwtUtil, loginRequestDto, httpServletResponse));
    }

    @PutMapping("/users/{email}")
    public ResponseEntity<UserResponseDto> update(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                                  @PathVariable String email,
                                                  @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        jwtUtil.checkAuth(tokenValue, email);
        return ResponseEntity.ok(userService.update(email, userUpdateRequestDto));
    }

    @DeleteMapping("/users")
    public String delete(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return userService.delete(loginRequestDto);
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<UserResponseDto> findProfile(@PathVariable String email) {
        return ResponseEntity.ok(userService.findProfile(email));
    }

    @PostMapping("/users/{email}/image")
    public ResponseEntity<?> createUsersImage(@PathVariable String email, @RequestPart("multipartFile") List<MultipartFile> multipartFile) throws IOException {
        try {
            return ResponseEntity.ok(userService.createUsersImage(email, multipartFile));  // 성공 시 HTTP 200 OK와 함께 이미지 경로 반환
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
        @PutMapping("/users/{email}/image")
        public List<String> modifyUsersImage (@PathVariable String
        email, @RequestPart(value = "multipartFile", required = false) List < MultipartFile > multipartFile) throws
        IOException {
            return userService.modifyUsersImage(email, multipartFile);
        }
    }
