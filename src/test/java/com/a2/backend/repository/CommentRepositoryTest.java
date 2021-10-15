package com.a2.backend.repository;

import com.a2.backend.BackendApplication;
import com.a2.backend.entity.Comment;
import com.a2.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebClient
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({BackendApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CommentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    User user =
            User.builder()
                    .nickname("nickname")
                    .email("some@email.com")
                    .biography("bio")
                    .password("password")
                    .build();

    LocalDateTime date = LocalDateTime.now();
    Comment comment = Comment.builder().comment("comment").user(user).date(date).build();

    @Test
    void Test001_CommentRepositoryShouldSaveComments() {
        userRepository.save(user);

        assertTrue(commentRepository.findAll().isEmpty());

        Comment savedComment = commentRepository.save(comment);
        assertNotNull(savedComment.getId());
        assertEquals(user, savedComment.getUser());
        assertEquals("comment", savedComment.getComment());
        assertEquals(date, savedComment.getDate());

        assertEquals(1, commentRepository.findAll().size());
    }
}
