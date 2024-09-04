package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.relation.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    Optional<Relationship> findBySentUserAndReceivedUser(User sentUser, User receivedUser);
    void deleteBySentUserAndReceivedUser(User sentUser, User receivedUser);
    void deleteBySentUserIdOrReceivedUserId(Long sentUserId, Long receivedUserId);
}
