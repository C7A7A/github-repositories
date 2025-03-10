package io.github.c7a7a.githubrepositories.controllers;

import io.github.c7a7a.githubrepositories.data.RepositoryFullData;
import io.github.c7a7a.githubrepositories.exceptions.ExceptionResponse;
import io.github.c7a7a.githubrepositories.exceptions.RateLimitExceededException;
import io.github.c7a7a.githubrepositories.exceptions.UserNotFoundException;
import io.github.c7a7a.githubrepositories.services.RepositoryService;
import io.smallrye.mutiny.Multi;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/repositories")
public class RepositoryController {
    private final RepositoryService service;

    public RepositoryController(RepositoryService service) {
        this.service = service;
    }

    @GetMapping
    public Multi<RepositoryFullData> getRepositories(@RequestParam String user) {
        return service.getRepositories(user);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleUserNotFoundException(UserNotFoundException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleRateLimitExceededException(RateLimitExceededException e) {
        return new ExceptionResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }
}
