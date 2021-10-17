package com.a2.backend.controller;

import com.a2.backend.constants.SecurityConstants;
import com.a2.backend.model.DiscussionUpdateDTO;
import com.a2.backend.service.DiscussionService;
import java.util.UUID;
import javax.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discussion")
public class DiscussionController {
    private final DiscussionService discussionService;

    public DiscussionController(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscussion(
            @Valid @RequestBody DiscussionUpdateDTO discussionUpdateDTO, @PathVariable UUID id) {
        val updatedDiscussion = discussionService.updateDiscussion(id, discussionUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDiscussion);
    }
}
