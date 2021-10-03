package com.a2.backend.service.impl;

import com.a2.backend.entity.*;
import com.a2.backend.exception.InvalidProjectCollaborationApplicationException;
import com.a2.backend.exception.ProjectNotFoundException;
import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectDTO;
import com.a2.backend.model.ProjectSearchDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.repository.ForumTagRepository;
import com.a2.backend.repository.LanguageRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.repository.TagRepository;
import com.a2.backend.service.*;
import java.util.*;
import javax.transaction.Transactional;
import lombok.val;
import org.springframework.stereotype.Service;

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

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            TagService tagService,
            LanguageService languageService,
            UserService userService,
            LanguageRepository languageRepository,
            TagRepository tagRepository,
            ForumTagService forumTagService,
            ForumTagRepository forumTagRepository) {
        this.projectRepository = projectRepository;
        this.tagService = tagService;
        this.languageService = languageService;
        this.userService = userService;
        this.languageRepository = languageRepository;
        this.tagRepository = tagRepository;
        this.forumTagService = forumTagService;
        this.forumTagRepository = forumTagRepository;
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
                            .languages(languages)
                            .owner(loggedUser)
                            .applicants(List.of())
                            .collaborators(List.of())
                            .rejectedApplicants(List.of())
                            .build();
            return projectRepository.save(project);
        }

        throw new ProjectWithThatTitleExistsException(
                String.format(
                        "There is an existing project named %s", projectCreateDTO.getTitle()));
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    @Transactional
    public Project updateProject(ProjectUpdateDTO projectUpdateDTO, UUID projectToBeUpdatedID) {
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
        return updatedProject;
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
    public Project getProjectDetails(UUID projectID) {
        return projectRepository
                .findById(projectID)
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

    public List<Project> searchProjectsByFilter(ProjectSearchDTO projectSearchDTO) {
        ArrayList<String> validLanguages = new ArrayList<>();
        ArrayList<String> validTags = new ArrayList<>();
        ArrayList<String> validForumTags = new ArrayList<>();
        ArrayList<Project> result = new ArrayList<>();
        boolean nullTitle = true;
        boolean nullTags = true;
        boolean nullLangs = true;
        boolean nullForumTags = true;
        boolean featured = projectSearchDTO.isFeatured();
        boolean nullPage = projectSearchDTO.getPage() == -1;
        if (projectSearchDTO.getTitle() != null) {
            nullTitle = false;
            result.addAll(
                    projectRepository.findByTitleContainingIgnoreCase(projectSearchDTO.getTitle()));
        }
        if (projectSearchDTO.getLanguages() != null && !projectSearchDTO.getLanguages().isEmpty()) {
            nullLangs = false;
            List<String> languages = projectSearchDTO.getLanguages();
            for (String language : languages) {
                result.addAll(
                        projectRepository.findProjectsByLanguageName(
                                language.toUpperCase(Locale.ROOT)));
                validLanguages.addAll(
                        languageRepository.findLanguageName(language.toUpperCase(Locale.ROOT)));
            }
        }
        if (projectSearchDTO.getTags() != null && !projectSearchDTO.getTags().isEmpty()) {
            nullTags = false;
            List<String> tags = projectSearchDTO.getTags();
            for (String tag : tags) {
                result.addAll(
                        projectRepository.findProjectsByTagName(tag.toUpperCase(Locale.ROOT)));
                validTags.addAll(tagRepository.findTagName(tag.toUpperCase(Locale.ROOT)));
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
            ArrayList<String> languageNames = new ArrayList<>();
            ArrayList<String> tagNames = new ArrayList<>();
            ArrayList<String> forumTagNames = new ArrayList<>();
            if (!nullLangs) {
                for (int j = 0; j < result.get(i).getLanguages().size(); j++) {
                    languageNames.add(result.get(i).getLanguages().get(j).getName());
                }
            }
            if (!nullTags) {
                for (int j = 0; j < result.get(i).getTags().size(); j++) {
                    tagNames.add(result.get(i).getTags().get(j).getName());
                }
            }

            if (!nullTitle) {
                if (!result.get(i).getTitle().contains(projectSearchDTO.getTitle())) {
                    result.remove(i);
                    i--;
                    continue;
                }
            }
            if (featured) {
                if (!result.get(i).isFeatured()) result.remove(i);
            }
            if (!nullLangs) {
                if (Collections.disjoint(validLanguages, languageNames)) {
                    result.remove(i);
                    i--;
                    continue;
                }
            }
            if (!nullTags) {
                if (Collections.disjoint(validTags, tagNames)) {
                    result.remove(i);
                    i--;
                }
            }
            if (!nullForumTags) {
                if (Collections.disjoint(validForumTags, forumTagNames)) {
                    result.remove(i);
                    i--;
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

        return result;
    }

    @Override
    public List<Project> getFeaturedProject() {
        return projectRepository.findAllByFeaturedIsTrue();
    }

    @Override
    public List<Project> getMyProjects() {
        val user = userService.getLoggedUser();
        return projectRepository.findByOwner(user);
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
        if (project.getRejectedApplicants().contains(loggedUser)) {
            throw new InvalidProjectCollaborationApplicationException(
                    String.format(
                            "Already rejected collaboration in project: %s", project.getTitle()));
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
    public List<Project> getProjectsByOwner(User owner) {
        return projectRepository.findByOwner(owner);
    }

    @Override
    public List<Project> getCollaboratingProjects(User user) {
        return projectRepository.findByCollaboratorsContaining(user);
    }
}
