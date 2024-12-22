package dev.william.willson.post.controller;

import dev.william.willson.globalException.ResourceNotFoundException;
import dev.william.willson.post.Post;
import dev.william.willson.post.PostController;
import dev.william.willson.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(PostController.class)  // Use the Controller class, not the test class
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostRepository postRepository;

    List<Post> posts = new ArrayList<>();

    @BeforeEach
    public void setup() {
        // create some posts
        posts = List.of(
                new Post(1, 1, "Hello world", "This is my first post.", null),
                new Post(2, 1, "New Post Again!", "This is my second post.", null)
        );
    }

    @Test
    public void PostController_FindAllPosts_ReturnsPost() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id": 1,
                        "userId": 1,
                        "title": "Hello world",
                        "body": "This is my first post.",
                        "version": null
                    },
                    {
                        "id": 2,
                        "userId": 1,
                        "title": "New Post Again!",
                        "body": "This is my second post.",
                        "version": null
                    }
                ]
                """;

        when(postRepository.findAll()).thenReturn(posts);

        ResultActions response = mockMvc.perform(get("/api/posts")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(jsonResponse));
    }

    // api/posts/1 -> success
    @Test
    public void PostController_FindPostById_ReturnsPost() throws Exception {
        int id = 1;
        when(postRepository.findById(id)).thenReturn(Optional.of(posts.get(id)));

        var post = posts.get(id);
        String json = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        ResultActions response = mockMvc.perform(get("/api/posts/{id}", id));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json));
    }

    // api/posts/999 -> failed
    @Test
    public void PostController_FindPostById_ReturnsNotFound() throws Exception {
        int invalidId = 999;
        when(postRepository.findById(invalidId)).thenThrow(ResourceNotFoundException.class);

        ResultActions response = mockMvc.perform(get("/api/posts/{id}", invalidId));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void PostController_CreatePost_ReturnsCreatedPost() throws Exception {
        var post = new Post(3, 1, "New Title", "This is a new post.", null);

        when(postRepository.save(post)).thenReturn(post);

        String json = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        ResultActions response = mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(json));
    }

    @Test
    public void PostController_CreatePostWhenPostIsInvalid_ReturnsBadRequest() throws Exception {
        var post = new Post(3, 1, "", "", null);

        when(postRepository.save(post)).thenReturn(post);

        String json = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        ResultActions response = mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void PostController_UpdatePost_ReturnsUpdatedPost() throws Exception {
        Post updatedPost = new Post(3, 1, "Updated Title", "This is a updated post.", null);

        when(postRepository.findById(updatedPost.id())).thenReturn(Optional.of(updatedPost));
        when(postRepository.save(updatedPost)).thenReturn(updatedPost);

        String requestBody = STR."""
                {
                    "id":\{updatedPost.id()},
                    "userId":\{updatedPost.userId()},
                    "title":"\{updatedPost.title()}",
                    "body":"\{updatedPost.body()}",
                    "version": null
                }
                """;

        ResultActions response = mockMvc.perform(put("/api/posts/{id}", updatedPost.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(requestBody));
    }

    @Test
    public void PostController_UpdatePostGivenInvalidUpdatedPost_ReturnsBadRequest() throws Exception {
        Post updatedPost = new Post(3, 1, "", "", null);

        when(postRepository.findById(updatedPost.id())).thenReturn(Optional.of(updatedPost));
        when(postRepository.save(updatedPost)).thenReturn(updatedPost);

        String requestBody = STR."""
                {
                    "id":\{updatedPost.id()},
                    "userId":\{updatedPost.userId()},
                    "title":"\{updatedPost.title()}",
                    "body":"\{updatedPost.body()}",
                    "version": null
                }
                """;

        ResultActions response = mockMvc.perform(put("/api/posts/{id}", updatedPost.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void PostController_DeletePostById_ReturnsIsDeleted() throws Exception {
        int id = 1;
        doNothing().when(postRepository).deleteById(id);

        ResultActions response = mockMvc.perform(delete("/api/posts/{id}", id));
        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
