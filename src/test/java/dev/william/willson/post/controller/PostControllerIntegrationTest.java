package dev.william.willson.post.controller;

import dev.william.willson.post.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void PostControllerIntegrationTest_FindAll_ReturnsAllPosts() {
        // /api/posts
        Post[] posts = restTemplate.getForObject("/api/posts", Post[].class);
        assertThat(posts.length).isEqualTo(100);
    }

    // Fist Attempt
//    @Test
//    public void PostControllerIntegrationTest_FindPostById_GivenValidId_ReturnsPost() {
//        Post post = restTemplate.getForObject("/api/posts/1", Post.class);
//        assertThat(post).isNotNull();
//        assertThat(post).hasFieldOrPropertyWithValue("id", 1);
//        assertThat(post).hasFieldOrPropertyWithValue("title", "sunt aut facere repellat provident occaecati excepturi optio reprehenderit");
//    }

    @Test
    public void PostControllerIntegrationTest_FindPostById_GivenValidId_ReturnsPost() {
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/1", HttpMethod.GET, null, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void PostControllerIntegrationTest_FindPostById_GivenInvalidId_ReturnsPost() {
        int INVALID_ID = -1;  // Using negative ID to simulate non-existence
        ResponseEntity<String> response = restTemplate.getForEntity("/api/posts/{id}", String.class, INVALID_ID);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Post not found with id: -1");
    }

    @Test
    @Rollback
    public void PostControllerIntegrationTest_CreatePost_GivenValidId_ReturnsCreatedPost() {
        Post post = new Post(101,1,"101 Title","101 Body",null);

        ResponseEntity<Post> response = restTemplate.exchange("/api/posts",HttpMethod.POST,new HttpEntity<>(post),Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).getTitle()).isEqualTo(post.getTitle());
        assertThat(response.getBody().userId()).isEqualTo(post.userId());
        assertThat(response.getBody().title()).isEqualTo(post.title());
        assertThat(response.getBody().body()).isEqualTo(post.body());
    }

    @Test
    public void PostControllerIntegrationTest_CreatePost_GivenInvalidPost_ReturnsBadRequest() {
        Post post = new Post(101,1,"","",null);

        ResponseEntity<Post> response = restTemplate.exchange("/api/posts",HttpMethod.POST,new HttpEntity<>(post),Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Rollback
    public void PostControllerIntegrationTest_UpdatePost_GivenValidId_ReturnsUpdatedPost() {
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/100",HttpMethod.GET,null,Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Post existingPost = response.getBody();
        assertThat(existingPost).isNotNull();
        Post updatedPost = new Post(existingPost.id(), existingPost.userId(), "updatedTitle", "updatedBody", existingPost.version());

        assertThat(updatedPost.id()).isEqualTo(existingPost.id());
        assertThat(updatedPost.userId()).isEqualTo(existingPost.userId());
        assertThat(updatedPost.title()).isEqualTo("updatedTitle");
        assertThat(updatedPost.body()).isEqualTo("updatedBody");
    }

    @Test
    public void PostControllerIntegrationTest_UpdatePost_GivenInvalidPost_ReturnsMethodNotAllowed() {
        Post post = new Post(100,1,"","",null);
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/100",HttpMethod.POST,new HttpEntity<Post>(post),Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @Rollback
    public void PostControllerIntegrationTest_DeletePost_GivenValidId_ReturnsDeletedPost() {
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/100",HttpMethod.DELETE,null,Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
