package com.a2.backend.service.impl;

import com.a2.backend.entity.Review;
import com.a2.backend.exception.InvalidUserException;
import com.a2.backend.model.ReviewCreateDTO;
import com.a2.backend.repository.ReviewRepository;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.ReviewService;
import java.time.LocalDateTime;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(UserRepository userRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review createReview(ReviewCreateDTO reviewCreateDTO) {
        val collaborator = userRepository.findById(reviewCreateDTO.getCollaboratorID());
        if (collaborator.isEmpty()) {
            throw new InvalidUserException(
                    String.format(
                            "User with id %s not found", reviewCreateDTO.getCollaboratorID()));
        }

        Review review =
                Review.builder()
                        .collaborator(collaborator.get())
                        .comment(reviewCreateDTO.getComment())
                        .score(reviewCreateDTO.getScore())
                        .date(LocalDateTime.now())
                        .build();

        return review;
    }
}
