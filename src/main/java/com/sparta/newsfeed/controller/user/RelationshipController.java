package com.sparta.newsfeed.controller.user;

import com.sparta.newsfeed.dto.relationship.RelationshipRequestDto;
import com.sparta.newsfeed.dto.relationship.RelationshipResponseDto;
import com.sparta.newsfeed.dto.user.UserResponseDto;
import com.sparta.newsfeed.entity.relation.RelationshipStatusEnum;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.repository.RelationshipRepository;
import com.sparta.newsfeed.service.user.RelationshipService;
import com.sparta.newsfeed.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;
    private final JwtUtil jwtUtil;

    @PostMapping("/relationships")
    public String create(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                         @RequestParam String sentEmail,
                         @RequestParam String receivedEmail){
        jwtUtil.checkAuth(tokenValue, sentEmail);
        return relationshipService.create(sentEmail, receivedEmail);
    }

    @PutMapping("/relationships/accept")
    public String accept(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                         @RequestParam String sentEmail,
                         @RequestParam String receivedEmail){
        jwtUtil.checkAuth(tokenValue, receivedEmail);
        return relationshipService.updateStatus(sentEmail, receivedEmail, RelationshipStatusEnum.ACCEPTED);
    }

    @PutMapping("/relationships/refuse")
    public String refuse(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                         @RequestParam String sentEmail,
                         @RequestParam String receivedEmail){
        jwtUtil.checkAuth(tokenValue, receivedEmail);
        return relationshipService.updateStatus(sentEmail, receivedEmail, RelationshipStatusEnum.REFUSED);
    }

    @DeleteMapping("/relationships")
    public String delete(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                         @RequestParam String sentEmail,
                         @RequestParam String receivedEmail){
        jwtUtil.checkAuth(tokenValue, sentEmail);
        return relationshipService.delete(sentEmail, receivedEmail);
    }

}
