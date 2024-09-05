package com.sparta.newsfeed.exception;

public class DataNotFoundException extends IllegalArgumentException{
    public DataNotFoundException(String s) {
        super(s);
    }
}
