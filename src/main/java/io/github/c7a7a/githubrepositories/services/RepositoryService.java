package io.github.c7a7a.githubrepositories.services;

import io.github.c7a7a.githubrepositories.data.BranchData;
import io.github.c7a7a.githubrepositories.data.RepositoryBasicData;
import io.github.c7a7a.githubrepositories.data.RepositoryFullData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RepositoryService {
    private final GithubApiService githubApiService;

    public RepositoryService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    public List<RepositoryFullData> getRepositories(String user) {
        List<RepositoryBasicData> allBasicData = githubApiService.getPublicRepositories(user);
        List<RepositoryFullData> allFullData = new ArrayList<>();

        for (RepositoryBasicData repositoryData : allBasicData) {
            String owner = repositoryData.owner();
            String name = repositoryData.name();

            List<BranchData> branchData = githubApiService.getBranches(owner, name);
            allFullData.add(new RepositoryFullData(owner, name, branchData));
        }

        return allFullData;
    }
}
