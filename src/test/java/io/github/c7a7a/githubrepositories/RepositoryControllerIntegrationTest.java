package io.github.c7a7a.githubrepositories;

import io.github.c7a7a.githubrepositories.data.RepositoryFullData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GithubRepositoriesApplication.class)
@AutoConfigureWebTestClient
public class RepositoryControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Test
    public void testGetRepositories_happyPath() {
        String user = "C7A7A";

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/repositories")
                        .queryParam("user", user)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryFullData.class)
                .consumeWith(response -> {
                   RepositoryFullData repository = response.getResponseBody().getFirst();
                   assert repository != null;
                   assert repository.ownerLogin().equalsIgnoreCase(user);
                   assert !repository.branches().isEmpty();

                   RepositoryFullData githubProjectRepository = response.getResponseBody().stream()
                           .filter(r -> r.repositoryName().equals("github-repositories"))
                           .findFirst()
                           .orElse(null);

                   if (githubProjectRepository != null) {
                       assert githubProjectRepository.repositoryName().equals("github-repositories");
                       assert githubProjectRepository.ownerLogin().equalsIgnoreCase(user);
                       assert githubProjectRepository.branches().size() == 1;
                       assert githubProjectRepository.branches().getFirst().name().equals("master");
                       assertThat(githubProjectRepository.branches().getFirst().lastCommitSha()).isNotEmpty();
                   }
                });
    }
}
