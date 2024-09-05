package com.sparta.newsfeed.exception;

public class CommentAuthOrVerificationException extends RuntimeException {
    public CommentAuthOrVerificationException(String message) {
        super(message);
    }
}