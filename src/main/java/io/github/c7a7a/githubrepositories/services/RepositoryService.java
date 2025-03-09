package io.github.c7a7a.githubrepositories.services;

import io.github.c7a7a.githubrepositories.data.RepositoryFullData;
import io.smallrye.mutiny.Multi;
import org.springframework.stereotype.Service;

@Service
public class RepositoryService {
    private final GithubApiService githubApiService;

    public RepositoryService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    public Multi<RepositoryFullData> getRepositories(String user) {
        return githubApiService.getPublicRepositories(user)
                .onItem()
                .transformToMultiAndConcatenate(repositoryData -> {
                    String owner = repositoryData.owner();
                    String name = repositoryData.name();

                    return githubApiService.getBranches(owner, name)
                            .collect()
                            .asList()
                            .onItem()
                            .transform(branchData -> new RepositoryFullData(owner, name, branchData))
                            .toMulti();
                });
    }
}
