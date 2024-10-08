package com.sparta.newsfeed.entity.post;

import lombok.Getter;

@Getter
public enum PostSortTypeEnum {
    RECENT(PostSortTypeEnum.Type.RECENT),
    LIKE(PostSortTypeEnum.Type.LIKE);
    private final String type;

    PostSortTypeEnum(String type) {
        this.type = type;
    }


    public static class Type {
        public static final String RECENT = "TYPE_RECENT";
        public static final String LIKE = "TYPE_LIKE";
    }
}
