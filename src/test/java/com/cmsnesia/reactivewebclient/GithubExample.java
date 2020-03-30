package com.cmsnesia.reactivewebclient;

import com.cmsnesia.reactivewebclient.annotation.PathVariable;
import com.cmsnesia.reactivewebclient.annotation.QueryParam;
import com.cmsnesia.reactivewebclient.annotation.Request;
import com.cmsnesia.reactivewebclient.annotation.WebfluxClient;
import com.cmsnesia.reactivewebclient.http.Method;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

//@SpringBootTest
class GithubExample {


    @WebfluxClient(name = "github")
    public interface GitHub {

        public class Repository {
            String name;
        }

        public class Contributor {
            String login;
        }

        public class Issue {

            Issue() {

            }

            String title;
            String body;
            List<String> assignees;
            int milestone;
            List<String> labels;
        }

        @Request(method = Method.GET, path = "/users/{username}/repos")
        Flux<Repository> repos(@PathVariable("username") String owner, @QueryParam("sort") String sort);

        @Request(method = Method.GET, path = "/repos/{owner}/{repo}/contributors")
        Flux<Contributor> contributors(@PathVariable("owner") String owner, @QueryParam("repo") String repo);

        @Request(method = Method.POST, path = "/repos/{owner}/{repo}/issues")
        Mono createIssue(Issue issue, @QueryParam("owner") String owner, @QueryParam("repo") String repo);

    }


    static class GitHubClientError extends RuntimeException {
        private String message; // parsed from json

        @Override
        public String getMessage() {
            return message;
        }
    }

    public static void main(String... args) {
//        final GitHub github = WebfluxClients.builder()
//                .target(GitHub.class, "https://api.github.com");

//        System.out.println("Let's fetch and print a list of the contributors to this org.");
//        Flux<GitHub.Repository> repositoryFlux = github.repos("openfeign", "full_name");
//        repositoryFlux.map(repository -> {
//            System.out.println(repository.name);
//            return repository.name;
//        });
//        System.out.println("Now, let's cause an error.");
//        try {
//            github.contributors("openfeign", "some-unknown-project");
//        } catch (final GitHubClientError e) {
//            System.out.println(e.getMessage());
//        }
//
//        System.out.println("Now, try to create an issue - which will also cause an error.");
//        try {
//            final GitHub.Issue issue = new GitHub.Issue();
//            issue.title = "The title";
//            issue.body = "Some Text";
//            github.createIssue(issue, "OpenFeign", "SomeRepo");
//        } catch (final GitHubClientError e) {
//            System.out.println(e.getMessage());
//        }
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json,application/vnd.github.v3+json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json,application/vnd.github.v3+json")
                .codecs(new Consumer<ClientCodecConfigurer>() {
                    @Override
                    public void accept(ClientCodecConfigurer clientCodecConfigurer) {
                        clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder());
                        clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder());
                    }
                })
                .build();
        webClient.get().uri("/users/ardikars/repos")
                .retrieve()
                .bodyToFlux(GitHub.Repository.class)
                .map(repository -> {
                   return repository.name;
                })
                .subscribe(System.out::println);
    }

}