package dev.william.willson.post.json;

import dev.william.willson.post.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class PostJsonTest {

    @Autowired
    private JacksonTester<Post> jacksonTester;

    @Test
    public void PostJsonTest_SerializePost_ReturnsPost() throws Exception {
        Post post = new Post(1,1,"NEW POST!","CONTENT BODY.", null);

        String expectedJson = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;
        assertThat(jacksonTester.write(post)).isEqualToJson(expectedJson);
    }

    @Test
    public void PostJsonTest_DeserializePost_ReturnsPost() throws Exception {
        Post post = new Post(1,1,"Test Title","Happy Coding!",null);

        String content = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        assertThat(jacksonTester.parse(content)).isEqualTo(post);
        assertThat(jacksonTester.parseObject(content).id()).isEqualTo(post.id());
        assertThat(jacksonTester.parseObject(content).userId()).isEqualTo(post.userId());
        assertThat(jacksonTester.parseObject(content).title()).isEqualTo(post.title());
        assertThat(jacksonTester.parseObject(content).body()).isEqualTo(post.body());
        assertThat(jacksonTester.parseObject(content).version()).isEqualTo(post.version());
    }
}
