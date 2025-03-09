package io.github.c7a7a.githubrepositories.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.c7a7a.githubrepositories.data.BranchData;
import io.github.c7a7a.githubrepositories.data.RepositoryBasicData;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GithubApiService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GithubApiService(WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder.baseUrl("https://api.github.com").build();
        this.objectMapper = objectMapper;
    }

    public List<RepositoryBasicData> getPublicRepositories(String user) {
        String response = webClient.get()
                .uri("/users/{user}/repos", user)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractRepositoryData(response);
    }

    public List<BranchData> getBranches(String owner, String repository) {
        String response = webClient.get()
                .uri("/repos/{owner}/{repository}/branches", owner, repository)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractBranchData(response);
    }

    private List<BranchData> extractBranchData(String jsonResponse) {
        List<BranchData> branches = new ArrayList<>();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            for (JsonNode node: jsonNode) {
                String name = node.path("name").asText();
                String sha = node.path("commit").path("sha").asText();
                branches.add(new BranchData(name, sha));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON branches response", e);
        }

        return branches;
    }

    private List<RepositoryBasicData> extractRepositoryData(String jsonResponse) {
        List<RepositoryBasicData> repositories = new ArrayList<>();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            for (JsonNode node : jsonNode) {
                boolean isForked = node.path("fork").asBoolean();
                if (isForked) continue;

                String repoName = node.path("name").asText();
                String owner = node.path("owner").path("login").asText();
                repositories.add(new RepositoryBasicData(repoName, owner));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON repositories response", e);
        }

        return repositories;
    }

}
