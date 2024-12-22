package dev.william.willson.post;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface PostRepository extends ListCrudRepository<Post, Integer> {
    List<Post> findByTitle(String title);
}
