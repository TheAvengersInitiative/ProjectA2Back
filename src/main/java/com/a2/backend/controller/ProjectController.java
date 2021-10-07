package com.a2.backend.controller;

import com.a2.backend.constants.SecurityConstants;
import com.a2.backend.entity.ForumTag;
import com.a2.backend.entity.Tag;
import com.a2.backend.model.*;
import com.a2.backend.service.DiscussionService;
import com.a2.backend.service.ForumTagService;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.TagService;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final TagService tagService;
    private final ForumTagService forumTagService;
    private final DiscussionService discussionService;

    public ProjectController(
            ForumTagService forumTagService,
            ProjectService projectService,
            TagService tagService,
            DiscussionService discussionService) {
        this.projectService = projectService;
        this.tagService = tagService;
        this.discussionService = discussionService;
        this.forumTagService = forumTagService;
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PostMapping
    public ResponseEntity<?> postNewProject(@Valid @RequestBody ProjectCreateDTO projectCreateDTO) {
        val createdProject = projectService.createProject(projectCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping
    public ResponseEntity<?> getAllProjects() {
        val projects = projectService.getAllProjects();
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PostMapping("/search")
    public ResponseEntity<?> getProjectsByNameSearch(
            @Valid @RequestBody ProjectSearchDTO projectSearchDTO) {
        val projects = projectService.searchProjectsByFilter(projectSearchDTO);
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @Valid @RequestBody ProjectUpdateDTO projectUpdateDTO, @PathVariable UUID id) {
        val updatedProject = projectService.updateProject(projectUpdateDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProject);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectDetails(@PathVariable UUID id) {
        val projectDetails = projectService.getProjectDetails(id);
        return ResponseEntity.status(HttpStatus.OK).body(projectDetails);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping("/languages")
    public ResponseEntity<?> getValidLanguages() {
        val validLanguages = projectService.getValidLanguageNames();
        return ResponseEntity.status(HttpStatus.OK).body(validLanguages);
    }

    @GetMapping("/tags")
    public ResponseEntity<?> getTags() {
        return ResponseEntity.ok(
                tagService.getAllTags().stream().map(Tag::getName).collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<?> getFeaturedProject() {
        val featuredProjects = projectService.getFeaturedProject();
        return ResponseEntity.status(HttpStatus.OK).body(featuredProjects);
    }

    @GetMapping("/my-projects")
    @Secured({SecurityConstants.USER_ROLE})
    public ResponseEntity<?> getMyProjects() {
        return ResponseEntity.ok(projectService.getMyProjects());
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/apply/{id}")
    public ResponseEntity<?> applyToProject(@PathVariable UUID id) {
        val appliedProject = projectService.applyToProject(id);
        return ResponseEntity.status(HttpStatus.OK).body(appliedProject);
    }

    @GetMapping("/forumtags")
    public ResponseEntity<?> getForumTags() {
        return ResponseEntity.ok(
                forumTagService.getAllTags().stream()
                        .map(ForumTag::getName)
                        .collect(Collectors.toList()));
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PostMapping("/{id}/discussion")
    public ResponseEntity<?> postNewDiscussion(
            @Valid @RequestBody DiscussionCreateDTO discussionCreateDTO, @PathVariable UUID id) {
        val createdDiscussion = discussionService.createDiscussion(id, discussionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDiscussion);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping("/applicants/{id}")
    public ResponseEntity<?> getProjectApplicants(@PathVariable UUID id) {
        val projectApplicants = projectService.getProjectApplicants(id);
        return ResponseEntity.status(HttpStatus.OK).body(projectApplicants);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/accept/{projectId}/{userId}")
    public ResponseEntity<?> acceptApplicant(
            @PathVariable UUID projectId, @PathVariable UUID userId) {
        val applicants = projectService.acceptApplicant(projectId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(applicants);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/reject/{projectId}/{userId}")
    public ResponseEntity<?> rejectApplicant(
            @PathVariable UUID projectId, @PathVariable UUID userId) {
        val applicants = projectService.rejectApplicant(projectId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(applicants);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/review/{id}")
    public ResponseEntity<?> reviewCollaborator(
            @Valid @RequestBody ReviewCreateDTO reviewCreateDTO, @PathVariable UUID id) {
        val review = projectService.createReview(id, reviewCreateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(review);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping("/reviews/{projectId}/{userId}")
    public ResponseEntity<?> getUserReviewsInProject(
            @PathVariable UUID projectId, @PathVariable UUID userId) {
        val reviews = projectService.getUserReviews(projectId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }
}
