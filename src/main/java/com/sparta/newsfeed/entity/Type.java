package com.sparta.newsfeed.entity;

public enum Type {
    USER(Authority.USER),
    POST(Authority.POST);

    private final String authority;

    Type(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "USER_IMAGE";
        public static final String POST = "POST_IMAGE";
    }
}