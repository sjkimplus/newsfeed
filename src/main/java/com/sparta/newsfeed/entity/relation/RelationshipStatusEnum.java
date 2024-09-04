package com.sparta.newsfeed.entity.relation;

import lombok.Getter;

@Getter
public enum RelationshipStatusEnum {
    WAITING("Waiting"),
    ACCEPTED("Accepted"),
    REFUSED("Refused");

    private final String status;

    RelationshipStatusEnum(String status){
        this.status = status;
    }

    public static String checkType(RelationshipStatusEnum status){
        for(RelationshipStatusEnum st : RelationshipStatusEnum.values()){
            if(st.getStatus().equals(status.getStatus())){
                return "유저가 " + st.getStatus() + " 했습니다.";
            }
        }
        return "RelationshipStatusEnum Type Error";
    }
}
