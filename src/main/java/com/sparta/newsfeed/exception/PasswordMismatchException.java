package com.sparta.newsfeed.exception;

public class PasswordMismatchException extends IllegalArgumentException{
    public PasswordMismatchException(String s) {
        super(s);
    }
}
