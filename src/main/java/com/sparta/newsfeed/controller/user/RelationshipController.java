package com.sparta.newsfeed.controller.user;

import com.sparta.newsfeed.annotation.Auth;
import com.sparta.newsfeed.dto.AuthUser;
import com.sparta.newsfeed.entity.relation.RelationshipStatusEnum;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.service.user.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    @PostMapping("/relationships")
    public String create(@Auth AuthUser authUser,
                         @RequestParam String receivedEmail){
        return relationshipService.create(authUser.getEmail(), receivedEmail);
    }

    @PutMapping("/relationships/accept")
    public String accept(@Auth AuthUser authUser,
                         @RequestParam String sentEmail){
        return relationshipService.updateStatus(authUser.getEmail(), sentEmail, RelationshipStatusEnum.ACCEPTED);
    }

    @PutMapping("/relationships/refuse")
    public String refuse(@Auth AuthUser authUser,
                         @RequestParam String sentEmail){
        return relationshipService.updateStatus(authUser.getEmail(), sentEmail, RelationshipStatusEnum.REFUSED);
    }

    @DeleteMapping("/relationships")
    public String delete(@Auth AuthUser authUser,
                         @RequestParam String targetEmail){
        return relationshipService.delete(authUser.getEmail(), targetEmail);
    }

}
