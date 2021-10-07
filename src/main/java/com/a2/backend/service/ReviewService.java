package com.a2.backend.service;

import com.a2.backend.entity.Review;
import com.a2.backend.model.ReviewCreateDTO;

public interface ReviewService {

    Review createReview(ReviewCreateDTO reviewCreateDTO);
}
