package dev.william.willson.post.repository;

import dev.william.willson.post.Post;
import dev.william.willson.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private PostRepository postRepository;

    @Test
    public void PostRepositoryTest_EstablishConnection_ReturnsIsTrue() throws Exception {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp() {
        // create some posts
        List<Post> posts = List.of(
                new Post(1, 1, "Hello, new world.", "This is a test case.", null),
                new Post(2, 2, "Hello, new world.", "This is a test case.", null),
                new Post(3, 2, "Hello, new world.", "This is a test case.", null),
                new Post(4, 3, "Hello, new world. Good for sharing", "This is a test case.", null)
        );

        postRepository.saveAll(posts);
    }

    // Only focus on the custom repository test
    @Test
    public void PostRepositoryTest_FindPostsByTitle_ReturnsAllRelevantPosts() throws Exception {
        List<Post> posts = postRepository.findByTitle("Hello, new world.");
        assertThat(posts).hasSize(3); // Asserts that the list has exactly 3 elements
        assertThat(posts).extracting(Post::getTitle).containsOnly("Hello, new world.");
    }


}
