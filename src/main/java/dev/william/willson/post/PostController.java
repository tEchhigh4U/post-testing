package dev.william.willson.post;

import dev.william.willson.globalException.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;

    // Constructor injection
    PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("")
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Post> findPostById(@PathVariable Integer id) {
        return  Optional.ofNullable(postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id)));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Post createPost(@RequestBody @Valid Post post) {
        return postRepository.save(post);
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Integer id, @RequestBody @Valid Post post) {
        Optional<Post> existingPost = postRepository.findById(id);
        if (existingPost.isPresent()) {
            Post updatedPost = new Post(
                    existingPost.get().id(),
                    existingPost.get().userId(),
                    post.title(),
                    post.body(),
                    existingPost.get().version()
            );
            return postRepository.save(updatedPost);
        } else {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Integer id) {
        postRepository.deleteById(id);
    }
}
