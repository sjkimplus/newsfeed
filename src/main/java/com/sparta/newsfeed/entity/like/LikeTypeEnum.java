package com.sparta.newsfeed.entity.like;

public enum LikeTypeEnum {
    POST(Type.POST),
    COMMENT(Type.COMMENT);
    private final String type;

    LikeTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static class Type {
        public static final String POST = "TYPE_POST";
        public static final String COMMENT = "TYPE_COMMENT";
    }
}