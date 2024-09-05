package com.sparta.newsfeed.controller.post;

import com.sparta.newsfeed.annotation.Auth;
import com.sparta.newsfeed.dto.AuthUser;
import com.sparta.newsfeed.dto.post.PageResponseDto;
import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.entity.post.PostSortTypeEnum;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.service.PostService;
import com.sparta.newsfeed.service.user.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.sparta.newsfeed.entity.post.PostSortTypeEnum.Type.RECENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
    private final PostService postService;
    private final RelationshipService relationshipService;
    private final JwtUtil jwtUtil;

    // 게시물 올리기
    @PostMapping(value = "/posts")
    public PostResponseDto createPost(@Auth AuthUser authUser,
                                      @RequestPart("requestDto") PostRequestDto requestDto,
                                      @RequestPart("multipartFile") List<MultipartFile> multipartFile) throws Exception {
        return postService.createPost(authUser.getEmail(), requestDto, multipartFile);
    }

    // 게시물 조회
    @GetMapping("/posts/{postId}")
    public PostResponseDto getPost(@Auth AuthUser authUser, @CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                   @PathVariable("postId") long postId) {
        // 본인확인
        jwtUtil.checkAuth(tokenValue, authUser.getEmail());

        // 본인의 게시물 및 친구의 게시물인지 확인
        PostResponseDto postResponseDto = postService.getPost(postId);
        String postOwnerEmail = postResponseDto.getEmail();
        boolean isOwner = Objects.equals(authUser.getEmail(), postOwnerEmail);
        boolean isFriend = relationshipService.checkFriend(authUser.getEmail(), postOwnerEmail);

        if (isOwner || isFriend) {
            return postResponseDto;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시물 조회권한이 없습니다");
        }
    }

    // 게시물 수정, 본인확인
    @PutMapping("/posts/{postId}")
    public void updatePost(@Auth AuthUser authUser,
                           @PathVariable("postId") long postId,
                           @RequestPart("requestDto") PostRequestDto requestDto) {

        PostResponseDto postResponseDto = postService.getPost(postId);
        // 수정하려는 게시물의 글쓴이와 현제 수정요청을 하는 유저가 동일한지 확인
        if (!postResponseDto.getEmail().equals(authUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없는 게시물 입니다.");
        }

        postService.updatePost(postId, requestDto);
    }

    // 게시물 삭제, 본인확인
    @DeleteMapping("/posts/{postId}")
    public void deletePost(@Auth AuthUser authUser,
                           @PathVariable("postId") long postId) throws IOException {

        PostResponseDto postResponseDto = postService.getPost(postId);
        // 수정하려는 게시물의 글쓴이와 현제 삭제요청을 하는 유저가 동일한지 확인
        if (!postResponseDto.getEmail().equals(authUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없는 게시물 입니다.");
        }
        postService.deletePost(postId);
    }

    // 게시물 다건 조회
    @GetMapping("/posts")
    public Page<PageResponseDto> getPosts(@Auth AuthUser authUser,
                                          @RequestParam("feedUserEmail") String feedUserEmail,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size) {

        // 본인의 게시물 및 친구의 게시물인지 확인
        boolean isOwner = Objects.equals(authUser.getEmail(), feedUserEmail);
        boolean isFriend = relationshipService.checkFriend(authUser.getEmail(), feedUserEmail);

        if (isOwner || isFriend) {
            return postService.getPosts(feedUserEmail, page-1, size);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비공계 게시물 페이지 입니다. 친구 신청을 해보세요!");
        }
    }


    // 뉴스피드, 본인 + 친구꺼만 필터해서 띄움
    @GetMapping("/newsfeed")
    public Page<PageResponseDto> getNewsFeed(@Auth AuthUser authUser,
                                          @RequestParam("type") PostSortTypeEnum type, // 2종류: RECENT, LIKE
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        // 본인및 친구들의 최근피드 가져오기
        return postService.getNewsFeed(authUser.getEmail(), type, page-1, size);
    }

}