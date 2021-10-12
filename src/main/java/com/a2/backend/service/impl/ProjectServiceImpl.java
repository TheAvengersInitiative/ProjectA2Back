package com.a2.backend.service.impl;

import com.a2.backend.entity.*;
import com.a2.backend.exception.*;
import com.a2.backend.model.*;
import com.a2.backend.repository.ForumTagRepository;
import com.a2.backend.repository.LanguageRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.repository.TagRepository;
import com.a2.backend.service.*;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    private final TagService tagService;

    private final LanguageService languageService;

    private final UserService userService;

    private final TagRepository tagRepository;

    private final ForumTagService forumTagService;

    private final ForumTagRepository forumTagRepository;

    private final LanguageRepository languageRepository;

    private final ReviewService reviewService;

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            TagService tagService,
            LanguageService languageService,
            UserService userService,
            LanguageRepository languageRepository,
            TagRepository tagRepository,
            ForumTagService forumTagService,
            ForumTagRepository forumTagRepository,
            ReviewService reviewService) {
        this.projectRepository = projectRepository;
        this.tagService = tagService;
        this.languageService = languageService;
        this.userService = userService;
        this.languageRepository = languageRepository;
        this.tagRepository = tagRepository;
        this.forumTagService = forumTagService;
        this.forumTagRepository = forumTagRepository;
        this.reviewService = reviewService;
    }

    @Override
    @Transactional
    public Project createProject(ProjectCreateDTO projectCreateDTO) {
        val existingProjectWithTitle = projectRepository.findByTitle(projectCreateDTO.getTitle());
        User loggedUser = userService.getLoggedUser();
        if (existingProjectWithTitle.isEmpty()) {
            List<Tag> tags = tagService.findOrCreateTag(projectCreateDTO.getTags());
            List<ForumTag> forumTags =
                    forumTagService.findOrCreateTag(projectCreateDTO.getForumTags());
            List<Language> languages =
                    languageService.findOrCreateLanguage(projectCreateDTO.getLanguages());

            Project project =
                    Project.builder()
                            .title(projectCreateDTO.getTitle())
                            .description(projectCreateDTO.getDescription())
                            .links(projectCreateDTO.getLinks())
                            .tags(tags)
                            .forumTags(forumTags)
                            .featured(projectCreateDTO.isFeatured())
                            .languages(languages)
                            .owner(loggedUser)
                            .applicants(List.of())
                            .collaborators(List.of())
                            .reviews(List.of())
                            .build();
            return projectRepository.save(project);
        }

        throw new ProjectWithThatTitleExistsException(
                String.format(
                        "There is an existing project named %s", projectCreateDTO.getTitle()));
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(Project::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDTO updateProject(ProjectUpdateDTO projectUpdateDTO, UUID projectToBeUpdatedID) {
        val projectToModifyOptional = projectRepository.findById(projectToBeUpdatedID);
        if (projectToModifyOptional.isEmpty()) {
            throw new ProjectNotFoundException(
                    String.format(
                            "The project with that id: %s does not exist!", projectToBeUpdatedID));
        }
        if (projectRepository.findByTitle(projectUpdateDTO.getTitle()).isPresent())
            throw new ProjectWithThatTitleExistsException(
                    String.format(
                            "There is an existing project named %s", projectUpdateDTO.getTitle()));

        List<Tag> removedTags =
                tagService.getRemovedTags(
                        projectUpdateDTO.getTags(),
                        getProjectDetails(projectToBeUpdatedID).getTags());
        List<ForumTag> removedForumTags =
                forumTagService.getRemovedTags(
                        projectUpdateDTO.getTags(),
                        getProjectDetails(projectToBeUpdatedID).getForumTags());

        List<Language> removedLanguages =
                languageService.getRemovedLanguages(
                        projectUpdateDTO.getLanguages(),
                        getProjectDetails(projectToBeUpdatedID).getLanguages());

        val project = projectToModifyOptional.get();
        project.setTitle(projectUpdateDTO.getTitle());
        project.setLinks(projectUpdateDTO.getLinks());
        project.setTags(tagService.findOrCreateTag(projectUpdateDTO.getTags()));
        project.setForumTags(forumTagService.findOrCreateTag(projectUpdateDTO.getForumTags()));
        project.setLanguages(languageService.findOrCreateLanguage(projectUpdateDTO.getLanguages()));
        project.setDescription(projectUpdateDTO.getDescription());

        Project updatedProject = projectRepository.save(project);
        tagService.deleteUnusedTags(removedTags);
        forumTagService.deleteUnusedTags(removedForumTags);
        languageService.deleteUnusedLanguages(removedLanguages);
        return updatedProject.toDTO();
    }

    @Override
    public void deleteProject(UUID uuid) {
        if (projectRepository.existsById(uuid)) {
            projectRepository.deleteById(uuid);
            return;
        }
        throw new ProjectNotFoundException(String.format("No project found for id: %s", uuid));
    }

    @Override
    public ProjectDTO getProjectDetails(UUID projectID) {
        return projectRepository
                .findById(projectID)
                .map(Project::toDTO)
                .orElseThrow(
                        () ->
                                new ProjectNotFoundException(
                                        String.format("No project found for id: %s", projectID)));
    }

    @Override
    @Transactional
    public void deleteProjectsFromUser(User owner) {
        projectRepository.deleteByOwner(owner);
    }

    @Override
    public List<String> getValidLanguageNames() {
        return languageService.getValidLanguages();
    }

    public List<ProjectDTO> searchProjectsByFilter(ProjectSearchDTO projectSearchDTO) {

        ArrayList<Project> result = new ArrayList<>();
        boolean featured = projectSearchDTO.isFeatured();
        boolean nullTitle = projectSearchDTO.getTitle() == null;
        boolean nullTags = projectSearchDTO.getTags() == null;
        boolean nullLangs = projectSearchDTO.getLanguages() == null;
        boolean nullPage = projectSearchDTO.getPage() == -1;
        List<String> upperCaseTagSearchFilters = new ArrayList<>();
        List<String> upperCaseLangSearchFilters = new ArrayList<>();

        if (!nullTags) {
            for (int j = 0; j < projectSearchDTO.getTags().size(); j++) {
                upperCaseTagSearchFilters.add(
                        projectSearchDTO.getTags().get(j).toUpperCase(Locale.ROOT));
            }
        }
        if (!nullLangs) {
            for (int j = 0; j < projectSearchDTO.getLanguages().size(); j++) {
                upperCaseLangSearchFilters.add(
                        projectSearchDTO.getLanguages().get(j).toUpperCase(Locale.ROOT));
            }
        }
        if (featured) {
            if (!nullTitle) {
                if (!nullLangs) {
                    result.addAll(
                            projectRepository.findProjectsByLanguagesInAndTitleAndFeatured(
                                    featured,
                                    projectSearchDTO.getTitle().toUpperCase(Locale.ROOT),
                                    upperCaseLangSearchFilters));
                }
                if (!nullTags) {
                    result.addAll(
                            projectRepository.findProjectsByTagsInAndTitleAndFeatured(
                                    featured,
                                    projectSearchDTO.getTitle().toUpperCase(Locale.ROOT),
                                    upperCaseTagSearchFilters));
                } else {
                    result.addAll(
                            projectRepository.findByTitleContainingIgnoreCaseAndFeatured(
                                    featured,
                                    projectSearchDTO.getTitle().toUpperCase(Locale.ROOT)));
                }
            } else if (!nullLangs) {
                result.addAll(
                        projectRepository.findProjectsByLanguagesInAndFeatured(
                                featured, upperCaseLangSearchFilters));
            }
            if (!nullTags) {
                result.addAll(
                        projectRepository.findProjectsByTagsInAndFeatured(
                                featured, upperCaseTagSearchFilters));
            }
            if (nullLangs && nullTags && nullTitle) {
                result.addAll(projectRepository.findAllByFeaturedIsTrue(featured));
            }
        } else {
            if (!nullTitle) {
                if (!nullLangs) {
                    result.addAll(
                            projectRepository.findProjectsByLanguagesInAndTitle(
                                    projectSearchDTO.getTitle().toUpperCase(Locale.ROOT),
                                    upperCaseLangSearchFilters));
                }
                if (!nullTags) {
                    result.addAll(
                            projectRepository.findProjectsByTagsInAndTitle(
                                    projectSearchDTO.getTitle().toUpperCase(Locale.ROOT),
                                    upperCaseTagSearchFilters));
                } else {
                    result.addAll(
                            projectRepository.findByTitleContainingIgnoreCase(
                                    projectSearchDTO.getTitle().toUpperCase(Locale.ROOT)));
                }
            } else if (!nullLangs) {
                result.addAll(
                        projectRepository.findProjectsByLanguagesIn(upperCaseLangSearchFilters));
            }
            if (!nullTags) {
                result.addAll(projectRepository.findProjectsByTagsIn(upperCaseTagSearchFilters));
            }
            if (nullLangs && nullTags && nullTitle) {
                result.addAll(projectRepository.findAllByFeaturedIsTrue(featured));
            }
        }

        for (int i = 0; i < result.size() - 1; i++) {
            for (int j = i + 1; j < result.size(); j++) {
                if (result.get(i).getId().equals(result.get(j).getId())) {
                    result.remove(i);
                    i--;
                    break;
                }
            }
        }
        for (int i = 0; i < result.size(); i++) {
            if (!nullTitle) {
                if (!result.get(i)
                        .getTitle()
                        .toUpperCase(Locale.ROOT)
                        .contains(projectSearchDTO.getTitle().toUpperCase(Locale.ROOT))) {
                    result.remove(i);
                    i--;
                    continue;
                }
            }

            if (!nullLangs) {
                List<String> upperCaseResults = new ArrayList<>();
                for (int j = 0; j < result.get(i).getLanguages().size(); j++) {
                    upperCaseResults.add(
                            result.get(i).getLanguages().get(j).getName().toUpperCase(Locale.ROOT));
                }
                if (result.get(i).getLanguages().size() < projectSearchDTO.getLanguages().size()
                        || !upperCaseResults.containsAll(upperCaseLangSearchFilters)) {
                    result.remove(i);
                    i--;
                    continue;
                }
            }
            if (!nullTags) {
                List<String> upperCaseResults = new ArrayList<>();
                for (int j = 0; j < result.get(i).getTags().size(); j++) {
                    upperCaseResults.add(
                            result.get(i).getTags().get(j).getName().toUpperCase(Locale.ROOT));
                }
                if (result.get(i).getTags().size() < projectSearchDTO.getTags().size()
                        || !upperCaseResults.containsAll(upperCaseTagSearchFilters)) {
                    result.remove(i);
                    i--;
                    continue;
                }
            }
        }
        if (!nullPage) {
            int page = projectSearchDTO.getPage();
            if (result.size() > 8 * (page)) {
                result.removeAll(result.subList(0, 8 * page));
                for (int i = 0; i < result.size(); i++) {}
            }
            if (result.size() > 8) {
                result.removeAll(result.subList(8, result.size()));
            }
        }

        return result.stream().map(Project::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getFeaturedProject() {
        return projectRepository.findAllByFeaturedIsTrue(true).stream()
                .map(Project::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getMyProjects() {
        val user = userService.getLoggedUser();
        return projectRepository.findByOwner(user).stream()
                .map(Project::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDTO applyToProject(UUID projectToApplyID) {
        User loggedUser = userService.getLoggedUser();
        val projectToApplyOptional = projectRepository.findById(projectToApplyID);

        if (projectToApplyOptional.isEmpty()) {
            throw new ProjectNotFoundException(
                    String.format(
                            "The project with that id: %s does not exist!", projectToApplyID));
        }

        val project = projectToApplyOptional.get();

        if (project.getCollaborators().contains(loggedUser)) {
            throw new InvalidProjectCollaborationApplicationException(
                    String.format("Already collaborating in project: %s", project.getTitle()));
        }

        if (project.getApplicants().contains(loggedUser)) {
            throw new InvalidProjectCollaborationApplicationException(
                    String.format("Already applied to project: %s", project.getTitle()));
        }
        if (project.getOwner().equals(loggedUser)) {
            throw new InvalidProjectCollaborationApplicationException(
                    String.format("Current user owns project: %s", project.getTitle()));
        }

        val applicants = project.getApplicants();
        applicants.add(loggedUser);

        project.setApplicants(applicants);

        return projectRepository.save(project).toDTO();
    }

    @Override
    public List<ProjectDTO> getProjectsByOwner(User owner) {
        return projectRepository.findByOwner(owner).stream()
                .map(Project::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getCollaboratingProjects(User user) {
        return projectRepository.findByCollaboratorsContaining(user).stream()
                .map(Project::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectUserDTO> getProjectApplicants(UUID projectId) {
        User loggedUser = userService.getLoggedUser();
        val projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException(
                    String.format("The project with that id: %s does not exist!", projectId));
        }

        val project = projectOptional.get();

        if (!project.getOwner().equals(loggedUser)) {
            throw new InvalidUserException("Only project owners can see applicants");
        }

        val applicants = project.getApplicants();

        return applicants.stream().map(User::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProjectUserDTO> acceptApplicant(UUID projectId, UUID userId) {
        User loggedUser = userService.getLoggedUser();
        val projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException(
                    String.format("The project with that id: %s does not exist!", projectId));
        }

        val project = projectOptional.get();

        if (!project.getOwner().equals(loggedUser)) {
            throw new InvalidUserException("Only project owners can accept applicants");
        }

        if (!project.getApplicants().stream()
                .map(User::getId)
                .collect(Collectors.toList())
                .contains(userId)) {
            throw new InvalidUserException(
                    String.format("The applicants with that id: %s does not exist!", userId));
        }

        var updatedApplicants = project.getApplicants();
        updatedApplicants.remove(userService.getUser(userId));
        var updatedCollaborators = project.getCollaborators();
        updatedCollaborators.add(userService.getUser(userId));

        project.setApplicants(updatedApplicants);
        project.setCollaborators(updatedCollaborators);

        return projectRepository.save(project).getApplicants().stream()
                .map(User::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectUserDTO> rejectApplicant(UUID projectId, UUID userId) {
        User loggedUser = userService.getLoggedUser();
        val projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException(
                    String.format("The project with that id: %s does not exist!", projectId));
        }

        val project = projectOptional.get();

        if (!project.getOwner().equals(loggedUser)) {
            throw new InvalidUserException("Only project owners can reject applicants");
        }

        if (!project.getApplicants().stream()
                .map(User::getId)
                .collect(Collectors.toList())
                .contains(userId)) {
            throw new InvalidUserException(
                    String.format("The applicants with that id: %s does not exist!", userId));
        }

        var updatedApplicants = project.getApplicants();
        updatedApplicants.remove(userService.getUser(userId));

        project.setApplicants(updatedApplicants);

        return projectRepository.save(project).getApplicants().stream()
                .map(User::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO createReview(UUID projectId, ReviewCreateDTO reviewCreateDTO) {
        User loggedUser = userService.getLoggedUser();
        val projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException(
                    String.format("The project with that id: %s does not exist!", projectId));
        }

        val project = projectOptional.get();

        if (!project.getOwner().equals(loggedUser)) {
            throw new InvalidUserException("Only project owners can submit reviews");
        }

        if (!project.getCollaborators().stream()
                .map(User::getId)
                .collect(Collectors.toList())
                .contains(reviewCreateDTO.getCollaboratorID())) {
            throw new NotValidCollaboratorException(
                    String.format(
                            "The user with id: %s does not collaborate in project with id: %s",
                            reviewCreateDTO.getCollaboratorID(), projectId));
        }

        val reviews = project.getReviews();
        val review = reviewService.createReview(reviewCreateDTO);
        reviews.add(review);

        project.setReviews(reviews);
        projectRepository.save(project);

        userService.updateReputation(reviewCreateDTO.getCollaboratorID());

        return review.toDTO();
    }

    @Override
    public List<ReviewDTO> getUserReviews(UUID projectId, UUID userId) {

        User loggedUser = userService.getLoggedUser();
        val projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException(
                    String.format("The project with that id: %s does not exist!", projectId));
        }

        val project = projectOptional.get();

        if (!project.getOwner().equals(loggedUser)) {
            throw new InvalidUserException("Only project owners can see collaborator reviews");
        }

        List<ReviewDTO> reviewDTOS = new ArrayList<>();

        for (Review review : project.getReviews()) {
            if (review.getCollaborator().getId().equals(userId)) {
                reviewDTOS.add(review.toDTO());
            }
        }

        return reviewDTOS;
    }
}
