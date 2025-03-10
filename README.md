<h1> GitHub Repositories </h1>

<p>
This project is a Spring Boot application that uses the GitHub REST API to fetch non-fork repositories for a given user. For each repository, it retrieves the repository name, owner login, and all branches with their names and the last commit SHA.
</p>

<hr/>

<h2> Features </h2>
<ul>
    <li>
        <b> Single Endpoint: </b> Fetch repository information for a given GitHub user.
    </li>
    <li>
        <b> Non-Fork Repositories: </b> Only non-fork repositories are returned.
    </li>
    <li>
        <b> Branch Details: </b> Includes branch names and their last commit SHA.
    </li>    
    <li>
        <b> Error Handling: </b> Returns a 404 Not Found error if the user does not exist.
    </li>
    <li>
        <b> Integration test: </b>  Includes an integration test that calls the external GitHub API without mocks and asserts that the response data is correct.
    </li>
    <li>
        <b> GitHub Token Support: </b> Use a GitHub Fine-Grained Personal Access Token to increase the API rate limit (60 requests per hour for unauthenticated users).
    </li>
</ul>

<hr/>

<h2> API Endpoint </h2>
Get Repositories for a User
<ul>
    <li><b>URL: </b> /api/v1/repositories</li>
    <li><b>Method: </b> GET</li>
    <li><b>Query Parameter: </b> user (required): The GitHub username.</li>
</ul>

Example Successful Response (HTTP 200 OK) <br/>
`GET http://localhost:8080/api/v1/repositories?user=c7a7a`
```
[
    {
        "repositoryName": "airplane-lowpoly",
        "ownerLogin": "C7A7A",
        "branches": [
            {
                "name": "main",
                "lastCommitSha": "056223865e877ea67bc87125e89a2cbb07289f6e"
            }
        ]
    },
    {
        "repositoryName": "arrow-calcite-connector",
        "ownerLogin": "C7A7A",
        "branches": [
            {
                "name": "main",
                "lastCommitSha": "e10f851baddc0063b93869a56b1b1aac69ecfb01"
            }
        ]
    },
    {
        "repositoryName": "aStar",
        "ownerLogin": "C7A7A",
        "branches": [
            {
                "name": "main",
                "lastCommitSha": "50b06d53fe17dca54d068c8790c32cf4ed7bc2ae"
            }
        ]
    },
    ...
]
```

Example Error Response (HTTP 404 Not Found) <br/>
`GET http://localhost:8080/api/v1/repositories?user=c7a7aa`
```
{
    "status": 404,
    "message": "User c7a7aa was not found"
}
```

<h2> How to Run </h2>
<b> Clone the repository </b>

```
git clone git@github.com:C7A7A/github-repositories.git
cd github-repositories
```

<b> Set up Github access Token (Optional) </b> <br/>
1. Create token (<a href="https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens">Github tutorial</a>) <br/>
2. Set the token as an environment variable

```
export GITHUB_FINE_GRAINED_PERSONAL_ACCESS_TOKEN="your_token_here"
```
<b> Build the Project </b>

```
./gradlew build
```
<b> Run the application </b> 

```
./gradlew bootRun
```
<h2> Application properties </h2>
The <code>application.properties</code> file contains the following configuration:

```
github.token=${GITHUB_FINE_GRAINED_PERSONAL_ACCESS_TOKEN:}
```

<ul>
    <li>
        If the environment variable GITHUB_FINE_GRAINED_PERSONAL_ACCESS_TOKEN is set, it will be used for authenticated requests.
    </li>
    <li>
        If not, the app will use unauthenticated access.
    </li>
</ul>