package io.github.c7a7a.githubrepositories.controllers;

import io.github.c7a7a.githubrepositories.data.RepositoryFullData;
import io.github.c7a7a.githubrepositories.services.RepositoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repositories")
public class RepositoryController {
    private final RepositoryService service;

    public RepositoryController(RepositoryService service) {
        this.service = service;
    }

    @GetMapping
    private List<RepositoryFullData> getRepositories(@RequestParam String user) {
        return service.getRepositories(user);
    }
}
