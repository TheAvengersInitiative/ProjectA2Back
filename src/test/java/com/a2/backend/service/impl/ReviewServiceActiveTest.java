package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.exception.InvalidUserException;
import com.a2.backend.model.ReviewCreateDTO;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.ReviewService;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

class ReviewServiceActiveTest extends AbstractServiceTest {

    @Autowired private UserRepository userRepository;

    @Autowired private ReviewService reviewService;

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test001_ReviewServiceWhenReceivesNotValidCollaboratorIdShouldThrowException() {

        ReviewCreateDTO reviewCreateDTO =
                ReviewCreateDTO.builder()
                        .collaboratorID(UUID.randomUUID())
                        .score(5)
                        .comment(null)
                        .build();

        assertThrows(InvalidUserException.class, () -> reviewService.createReview(reviewCreateDTO));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test002_ReviewServiceWhenReceivesValidReviewCreateCTOShouldCreateReview() {
        val collaborator = userRepository.findByNickname("Peltevis");

        ReviewCreateDTO reviewCreateDTO =
                ReviewCreateDTO.builder()
                        .collaboratorID(collaborator.get().getId())
                        .score(5)
                        .comment(null)
                        .build();

        val createdReview = reviewService.createReview(reviewCreateDTO);

        assertNotNull(createdReview);

        assertEquals(createdReview.getCollaborator().getId(), reviewCreateDTO.getCollaboratorID());
        assertEquals(createdReview.getScore(), reviewCreateDTO.getScore());
        assertEquals(createdReview.getComment(), reviewCreateDTO.getComment());
    }
}
