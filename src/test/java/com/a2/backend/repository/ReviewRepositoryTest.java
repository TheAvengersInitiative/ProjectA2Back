package com.a2.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.BackendApplication;
import com.a2.backend.entity.Review;
import com.a2.backend.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@AutoConfigureWebClient
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({BackendApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ReviewRepositoryTest {

    @Autowired ReviewRepository reviewRepository;

    @Autowired UserRepository userRepository;

    User user =
            User.builder()
                    .nickname("nickname")
                    .email("some@email.com")
                    .biography("bio")
                    .password("hashed_password")
                    .build();

    User user2 =
            User.builder()
                    .nickname("nickname2")
                    .email("some2@email.com")
                    .biography("bio2")
                    .password("hashed_password")
                    .build();

    Review review =
            Review.builder()
                    .collaborator(user)
                    .score(4)
                    .comment("Insert valid comment")
                    .date(LocalDateTime.now())
                    .build();

    Review review2 =
            Review.builder()
                    .collaborator(user)
                    .score(2)
                    .comment(null)
                    .date(LocalDateTime.now())
                    .build();

    Review review3 =
            Review.builder()
                    .collaborator(user2)
                    .score(5)
                    .comment("Excellent job")
                    .date(LocalDateTime.now())
                    .build();

    @Test
    void Test001_ReviewRepositoryShouldSaveReviews() {
        userRepository.save(user);

        assertTrue(reviewRepository.findAll().isEmpty());

        assertNull(review.getId());

        reviewRepository.save(review);

        assertFalse(reviewRepository.findAll().isEmpty());

        List<Review> reviews = reviewRepository.findAll();

        assertEquals(1, reviews.size());

        System.out.println(review.getDate());

        val savedReview = reviews.get(0);

        assertNotNull(savedReview.getId());
        assertEquals("nickname", savedReview.getCollaborator().getNickname());
    }
}
