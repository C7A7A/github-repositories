package io.github.c7a7a.githubrepositories.data;

import java.util.List;

public record RepositoryFullData(String repositoryName, String ownerLogin, List<BranchData> branches) {
}
