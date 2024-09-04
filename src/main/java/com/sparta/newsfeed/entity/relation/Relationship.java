package com.sparta.newsfeed.entity.relation;

import com.sparta.newsfeed.entity.Timestamped;
import com.sparta.newsfeed.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "relationships")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Relationship extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_id")
    private User sentUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_id")
    private User receivedUser;

    @Enumerated(EnumType.STRING)
    private RelationshipStatusEnum status = RelationshipStatusEnum.WAITING;

    public Relationship(User sentUser, User receivedUser) {
        this.sentUser = sentUser;
        this.receivedUser = receivedUser;
    }

    public void update(RelationshipStatusEnum relationshipStatusEnum){
        this.status = relationshipStatusEnum;
    }
}
