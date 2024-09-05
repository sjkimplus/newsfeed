package com.sparta.newsfeed;

import com.sparta.newsfeed.entity.*;
import com.sparta.newsfeed.entity.post.Post;
import com.sparta.newsfeed.repository.PostCommentRepository;
import com.sparta.newsfeed.repository.PostRepository;
import com.sparta.newsfeed.repository.UserRepository;
import com.sparta.newsfeed.repository.imageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional(readOnly = true)
public class CommentTest {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final com.sparta.newsfeed.repository.imageRepository imageRepository;

    @Autowired
    CommentTest(UserRepository userRepository, PostRepository postRepository, PostCommentRepository postCommentRepository, imageRepository imageRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.imageRepository = imageRepository;
    }


    @Test
    void createdUser() {

        // Creating a User object with fake data
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setName("John Doe");
        user.setBirthday("1990-01-01");
        user.setPostQuantity(0L);
        user.setDateDeleted(null); // Optional field, can be null

        // Saving the User object to the database
        userRepository.save(user);

    }

    @Test
    void createdPost(){
        User user = new User();
        user.setId(2L);
        User user1 = userRepository.findById(user.getId()).orElseThrow(()->
                new EntityNotFoundException()
        );

        Post post = new Post();
        post.setContent("test url");
        post.setUser(user1);
        post.setCreatedDate(null);
        post.setModifiedDate(null);

        Image image = new Image();
        image.setCreatedAt(null);
        List<String> url = new ArrayList<>();
        url.add("https://notion-emojis.s3-us-west-2.amazonaws.com/prod/svg-twitter/1f340.svg");
        url.add("https://notion-emojis.s3-us-west-2.amazonaws.com/prod/svg-twitter/1f340.svg");
        url.add("https://notion-emojis.s3-us-west-2.amazonaws.com/prod/svg-twitter/1f340.svg");
        for (String image2: url) {
            image.addImageUrl(image2);
        }
        image.setType(Type.POST);
        image.setItemId(user1.getId());
        imageRepository.save(image);
        postRepository.save(post);
    }

    @Test
    void createdComment(){
        PostComment postComment = new PostComment();
        User user = new User();
        Post post = new Post();
        post.setId(4L);
        user.setId(2L);
        User user1 = userRepository.findById(user.getId()).orElseThrow(()->
                new EntityNotFoundException()
        );
        user.setId(2L);
        Post post1 = postRepository.findById(post.getId()).orElseThrow(()->
                new EntityNotFoundException()
        );
        postComment.setPost(post1);
        postComment.setContent("test comment");
        postComment.setUserId(user1.getId());
        postCommentRepository.save(postComment);
    }

    @Test
    @Transactional  // 트랜잭션을 명시적으로 추가
    @Rollback(false)
    void updateComment(){
        PostComment postComment = new PostComment();
        User user = new User();
        Post post = new Post();
        post.setId(4L);
        user.setId(2L);
        postComment.setId(1L);

        User user1 = userRepository.findById(user.getId()).orElseThrow(()->
                new EntityNotFoundException()
        );
        Post post1 = postRepository.findById(post.getId()).orElseThrow(()->
                new EntityNotFoundException()
        );
        PostComment postComment1 = postCommentRepository.findById(postComment.getId()).orElseThrow(() ->
                new EntityNotFoundException()
        );

        postComment1.commentsModify("변경!!!!!!!!");
        // postCommentRepository.save(postComment1);  // 이 부분이 없어도 더티 체킹이 작동해야 함
    }

    @Transactional
    @Test
    @Rollback(false)
    void deleteComment(){
        PostComment postComment = new PostComment();
        Post post = new Post();
        post.setId(4L);
        postComment.setId(1L);

        Post post1 = postRepository.findById(post.getId()).orElseThrow(()->
                new EntityNotFoundException()
        );
        PostComment postComment1 = postCommentRepository.findById(postComment.getId()).orElseThrow(() ->
                new EntityNotFoundException()
        );

        postCommentRepository.deleteById(postComment1.getId());
    }
}
