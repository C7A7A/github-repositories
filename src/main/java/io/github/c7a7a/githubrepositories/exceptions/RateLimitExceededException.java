package io.github.c7a7a.githubrepositories.exceptions;

public class RateLimitExceededException extends RuntimeException{
    public RateLimitExceededException(String message) {
        super(message);
    }
}
