package com.a2.backend.service.impl;

import com.a2.backend.entity.Language;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.Tag;
import com.a2.backend.entity.User;
import com.a2.backend.exception.ProjectNotFoundException;
import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.LanguageService;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.TagService;
import com.a2.backend.utils.SearchUtils.ProjectSpecificationBuilder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.transaction.Transactional;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    private final TagService tagService;

    private final LanguageService languageService;

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            TagService tagService,
            LanguageService languageService) {
        this.projectRepository = projectRepository;
        this.tagService = tagService;
        this.languageService = languageService;
    }

    @Override
    @Transactional
    public Project createProject(ProjectCreateDTO projectCreateDTO) {
        val existingProjectWithTitle = projectRepository.findByTitle(projectCreateDTO.getTitle());
        if (existingProjectWithTitle.isEmpty()
                || !existingProjectWithTitle.get().getOwner().equals(projectCreateDTO.getOwner())) {

            List<Tag> tags = tagService.findOrCreateTag(projectCreateDTO.getTags());
            List<Language> languages =
                    languageService.findOrCreateLanguage(projectCreateDTO.getLanguages());

            Project project =
                    Project.builder()
                            .title(projectCreateDTO.getTitle())
                            .description(projectCreateDTO.getDescription())
                            .links(projectCreateDTO.getLinks())
                            .tags(tags)
                            .languages(languages)
                            .owner(projectCreateDTO.getOwner())
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
    public Project updateProject(ProjectUpdateDTO projectUpdateDTO, UUID projectToBeUpdatedID) {
        val projectToModifyOptional = projectRepository.findById(projectToBeUpdatedID);
        if (projectToModifyOptional.isEmpty()) {
            throw new ProjectNotFoundException(
                    String.format(
                            "The project with that id: %s does not exist!", projectToBeUpdatedID));
        }

        List<Tag> removedTags =
                tagService.getRemovedTags(
                        projectUpdateDTO.getTags(),
                        getProjectDetails(projectToBeUpdatedID).getTags());

        List<Language> removedLanguages =
                languageService.getRemovedLanguages(
                        projectUpdateDTO.getLanguages(),
                        getProjectDetails(projectToBeUpdatedID).getLanguages());

        val project = projectToModifyOptional.get();
        project.setTitle(projectUpdateDTO.getTitle());
        project.setLinks(projectUpdateDTO.getLinks());
        project.setTags(tagService.findOrCreateTag(projectUpdateDTO.getTags()));
        project.setLanguages(languageService.findOrCreateLanguage(projectUpdateDTO.getLanguages()));
        project.setDescription(projectUpdateDTO.getDescription());

        Project updatedProject = projectRepository.save(project);
        tagService.deleteUnusedTags(removedTags);
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

    public List<Project> getProjectsByTitleSearch(String pattern, int pageNo) {
        Pageable paging = PageRequest.of(pageNo, 8, Sort.by("title").ascending());

        Page<Project> pagedResult =
                projectRepository.findByTitleContainingIgnoreCase(pattern, paging);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<Project>();
        }
    }

    @Override
    public List<String> getValidLanguageNames() {
        return languageService.getValidLanguages();
    }

    public List<Project> searchProjecsByFilter(String search) {
        Boolean title = false;
        ArrayList<Project> result = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<String> languages = new ArrayList<>();
        ProjectSpecificationBuilder builder = new ProjectSpecificationBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)(:)(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            if (matcher.group(1).equals("link")) {
                links.add(matcher.group(3));
            }
            if (matcher.group(1).equals("tag")) {
                tags.add(matcher.group(3));
            }
            if (matcher.group(1).equals("language")) {
                languages.add(matcher.group(3));
            }
            if (matcher.group(1).equals("title")) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
                title = true;
            }
        }
        Specification<Project> spec = builder.build();
        if (title) result.addAll(projectRepository.findAll(spec));
        for (String link : links) {
            result.addAll(projectRepository.findProjectsByLink(link));
        }
        for (String tag : tags) {
            result.addAll(projectRepository.findProjectsByTagName(tag));
        }
        for (String language : languages) {
            result.addAll(projectRepository.findProjectsByLanguageName(language));
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

        return result;
    }
}
