package com.a2.backend.controller;

import com.a2.backend.constants.SecurityConstants;
import com.a2.backend.model.CommentUpdateDTO;
import com.a2.backend.model.CommentCreateDTO;
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

    @Secured({SecurityConstants.USER_ROLE})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscussion(@PathVariable UUID id) {
        discussionService.deleteDiscussion(id);
        return ResponseEntity.noContent().build();
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/comment/{discussionId}")
    public ResponseEntity<?> createComment(
            @PathVariable UUID discussionId,
            @Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        val comment = discussionService.createComment(discussionId, commentCreateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/highlight/{commentId}")
    public ResponseEntity<?> highlightComment(@PathVariable UUID commentId) {
        val comment = discussionService.changeCommentHighlight(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/hide/{commentId}")
    public ResponseEntity<?> hideComment(@PathVariable UUID commentId) {
        val comment = discussionService.changeCommentHidden(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping("/comments/{discussionId}")
    public ResponseEntity<?> getCommentsInDiscussion(@PathVariable UUID discussionId) {
        val comments = discussionService.getComments(discussionId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @DeleteMapping("comment/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID id) {
        discussionService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/comment-update/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable UUID commentId, @Valid @RequestBody CommentUpdateDTO commentUpdateDTO) {
        val comment = discussionService.updateComment(commentId, commentUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }
}
