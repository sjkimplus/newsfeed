package com.sparta.newsfeed.dto.relationship;

import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.relation.Relationship;
import com.sparta.newsfeed.entity.relation.RelationshipStatusEnum;
import lombok.Getter;

@Getter
public class RelationshipResponseDto {

    private User sentUser;
    private User receivedUser;
    private RelationshipStatusEnum status;

    public RelationshipResponseDto(Relationship relationship) {
        this.sentUser = relationship.getSentUser();
        this.receivedUser = relationship.getReceivedUser();
        this.status = relationship.getStatus();
    }
}
