package com.sparta.newsfeed.entity.alarm;

public enum AlarmTypeEnum {
    COMMENT(Type.COMMENT),
    LIKE(Type.LIKE),
    RELATIONSHIP(Type.RELATIONSHIP);

    private final String type;

    AlarmTypeEnum(String type) {
        this.type = type;
    }

    public static class Type {
        public static final String COMMENT = "TYPE_COMMENT";
        public static final String LIKE = "TYPE_LIKE";
        public static final String RELATIONSHIP = "TYPE_RELATIONSHIP";
    }
}