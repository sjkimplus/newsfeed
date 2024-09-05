package com.sparta.newsfeed.service.user;

import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.relation.Relationship;
import com.sparta.newsfeed.entity.relation.RelationshipStatusEnum;
import com.sparta.newsfeed.exception.DataDuplicationException;
import com.sparta.newsfeed.exception.DataNotFoundException;
import com.sparta.newsfeed.repository.RelationshipRepository;
import com.sparta.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final UserRepository userRepository;

    @Transactional
    public String create(String sentEmail, String receivedEmail){

        //0 - sentUser, 1 - receivedUser
        List<User> users = checkUser(sentEmail, receivedEmail);

        //데이터 중복 방지
        if(findRelationship(users.get(0), users.get(1)).isPresent()) throw new DataDuplicationException("이미 친구 추가 요청을 보냈습니다.");

        //이미 상대방이 친구 요청을 했을경우
        Optional<Relationship> receivedRelationship = findRelationship(users.get(1), users.get(0));
        if(receivedRelationship.isPresent()) throw new DataDuplicationException("이미 상대방이 친구 추가 요청을 보냈습니다.");

        Relationship newRelationship = new Relationship(users.get(0), users.get(1));
        relationshipRepository.save(newRelationship);

        return "친구 요청이 완료되었습니다.";
    }

    @Transactional
    public String updateStatus(String ownerEmail, String sentEmail, RelationshipStatusEnum relationshipStatusEnum){
        //0 - sentUser, 1 - receivedUser
        List<User> users = checkUser(sentEmail, ownerEmail);

        Optional<Relationship> relationship = findRelationship(users.get(0), users.get(1));
        if(relationship.isEmpty()) throw new DataNotFoundException("존재하지 않는 친구 요청 입니다");
        if(!relationship.get().getStatus().equals(RelationshipStatusEnum.WAITING))
            return  "유저가 " + relationship.get().getStatus() + " 했습니다.";

        relationship.get().update(relationshipStatusEnum);

        return RelationshipStatusEnum.checkType(relationshipStatusEnum);
    }

    @Transactional
    public String delete(String ownerEmail, String targetEmail){
        //0 - sentUser, 1 - receivedUser
        List<User> users = checkUser(targetEmail, ownerEmail);

        Optional<Relationship> relationship = findRelationship(users.get(0), users.get(1));
        if(relationship.isEmpty()) throw new DataNotFoundException("친구가 아닙니다.");

        relationshipRepository.deleteBySentUserAndReceivedUser(users.get(0), users.get(1));

        return "친구 삭제가 완료 되었습니다.";
    }

    private Optional<Relationship> findRelationship(User sentUser, User receivedUser){
        return relationshipRepository.findBySentUserAndReceivedUser(sentUser, receivedUser);
    }

    private Optional<User> findUser(String email){
        return userRepository.findByEmail(email);
    }

    private List<User> checkUser(String sentEmail, String receivedEmail){
        Optional<User> sentUsers = findUser(sentEmail);
        Optional<User> receivedUsers = findUser(receivedEmail);
        if(sentUsers.isEmpty() || sentUsers.get().getDateDeleted() != null) throw new DataNotFoundException("존재하지 않는 아이디 입니다");
        if(receivedUsers.isEmpty() || receivedUsers.get().getDateDeleted() != null) throw new DataNotFoundException("존재하지 않는 아이디 입니다");

        List<User> temp = new ArrayList<>();
        temp.add(sentUsers.get());
        temp.add(receivedUsers.get());

        return temp;
    }
}
