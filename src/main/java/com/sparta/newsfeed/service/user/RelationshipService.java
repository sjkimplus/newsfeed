package com.sparta.newsfeed.service.user;

import com.sparta.newsfeed.dto.relationship.RelationshipRequestDto;
import com.sparta.newsfeed.dto.relationship.RelationshipResponseDto;
import com.sparta.newsfeed.dto.user.UserRequestDto;
import com.sparta.newsfeed.dto.user.UserResponseDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.relation.Relationship;
import com.sparta.newsfeed.entity.relation.RelationshipStatusEnum;
import com.sparta.newsfeed.repository.RelationshipRepository;
import com.sparta.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final UserRepository userRepository;

    @Transactional
    public String create(String sentEmail, String receivedEmail){
        Optional<User> sentUser = findUser(sentEmail);
        if(sentUser.isEmpty()) throw new IllegalArgumentException("존재하지 않는 아이디 입니다");
        Optional<User> receivedUser = findUser(receivedEmail);
        if(receivedUser.isEmpty()) throw new IllegalArgumentException("존재하지 않는 아이디 입니다");

        //데이터 중복 방지
        if(findRelationship(sentUser.get(), receivedUser.get()).isPresent()) throw new IllegalArgumentException("이미 친구 추가 요청을 보냈습니다.");

        //이미 상대방이 친구 요청을 했을경우
        Optional<Relationship> receivedRelationship = findRelationship(receivedUser.get(), sentUser.get());
        if(receivedRelationship.isPresent()) throw new IllegalArgumentException("이미 상대방이 친구 추가 요청을 보냈습니다.");

        Relationship newRelationship = new Relationship(sentUser.get(), receivedUser.get());
        relationshipRepository.save(newRelationship);

        return "친구 요청이 완료되었습니다.";
    }

    @Transactional
    public String updateStatus(String sentEmail, String receivedEmail, RelationshipStatusEnum relationshipStatusEnum){
        Optional<User> sentUser = findUser(sentEmail);
        if(sentUser.isEmpty()) throw new IllegalArgumentException("존재하지 않는 아이디 입니다");
        Optional<User> receivedUser = findUser(receivedEmail);
        if(receivedUser.isEmpty()) throw new IllegalArgumentException("존재하지 않는 아이디 입니다");
        Optional<Relationship> relationship = findRelationship(sentUser.get(), receivedUser.get());
        if(relationship.isEmpty()) throw new IllegalArgumentException("존재하지 않는 친구 요청 입니다");
        if(!relationship.get().getStatus().equals(RelationshipStatusEnum.WAITING))
            return  "유저가 " + relationship.get().getStatus() + " 했습니다.";

        relationship.get().update(relationshipStatusEnum);

        return RelationshipStatusEnum.checkType(relationshipStatusEnum);
    }

    @Transactional
    public String delete(String sentEmail, String receivedEmail){
        Optional<User> sentUser = findUser(sentEmail);
        if(sentUser.isEmpty()) throw new IllegalArgumentException("존재하지 않는 아이디 입니다");
        Optional<User> receivedUser = findUser(receivedEmail);
        if(receivedUser.isEmpty()) throw new IllegalArgumentException("존재하지 않는 아이디 입니다");
        Optional<Relationship> relationship = findRelationship(sentUser.get(), receivedUser.get());
        if(relationship.isEmpty()) throw new IllegalArgumentException("친구가 아닙니다.");

        relationshipRepository.deleteBySentUserAndReceivedUser(sentUser.get(), receivedUser.get());

        return "친구 삭제가 완료 되었습니다.";
    }

    private Optional<Relationship> findRelationship(User sentUser, User receivedUser){
        return relationshipRepository.findBySentUserAndReceivedUser(sentUser, receivedUser);
    }

    private Optional<User> findUser(String email){
        return userRepository.findByEmail(email);
    }
}
