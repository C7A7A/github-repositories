package io.github.c7a7a.githubrepositories.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.c7a7a.githubrepositories.data.BranchData;
import io.github.c7a7a.githubrepositories.data.RepositoryBasicData;
import io.github.c7a7a.githubrepositories.exceptions.UserNotFoundException;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class GithubApiService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GithubApiService(WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder.baseUrl("https://api.github.com").build();
        this.objectMapper = objectMapper;
    }

    public Multi<RepositoryBasicData> getPublicRepositories(String user) {
        return Uni.createFrom().completionStage(
                webClient.get()
                    .uri("/users/{user}/repos", user)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new UserNotFoundException("User " + user + " was not found"));
                        }
                        return Mono.error(new RuntimeException("Client error"));
                    })
                    .bodyToMono(String.class)
                    .toFuture()
        ).onItem().transformToMulti(this::extractRepositoryData);
    }

    public Multi<BranchData> getBranches(String owner, String repository) {
        return Uni.createFrom().completionStage(
                webClient.get()
                    .uri("/repos/{owner}/{repository}/branches", owner, repository)
                    .retrieve()
                    .bodyToMono(String.class)
                    .toFuture()
        ).onItem().transformToMulti(this::extractBranchData);
    }

    private Multi<BranchData> extractBranchData(String jsonResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            return Multi.createFrom().iterable(jsonNode::elements)
                    .map(node -> {
                        String name = node.path("name").asText();
                        String sha = node.path("commit").path("sha").asText();
                        return new BranchData(name, sha);
                    });
        } catch (IOException e) {
            return Multi.createFrom().failure(new RuntimeException("Failed to parse JSON branches response", e));
        }
    }

    private Multi<RepositoryBasicData> extractRepositoryData(String jsonResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            return Multi.createFrom().iterable(jsonNode::elements)
                    .filter(node -> !node.path("fork").asBoolean())
                    .map(node -> {
                        String repoName = node.path("name").asText();
                        String owner = node.path("owner").path("login").asText();
                        return new RepositoryBasicData(repoName, owner);
                    });
        } catch (IOException e) {
            return Multi.createFrom().failure(new RuntimeException("Failed to parse JSON repositories response", e));
        }
    }
}
