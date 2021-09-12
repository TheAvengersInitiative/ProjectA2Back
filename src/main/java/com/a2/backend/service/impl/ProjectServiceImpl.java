package com.a2.backend.service.impl;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.Tag;
import com.a2.backend.entity.User;
import com.a2.backend.exception.ProjectNotFoundException;
import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.TagService;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    private final TagService tagService;

    public ProjectServiceImpl(ProjectRepository projectRepository, TagService tagService) {
        this.projectRepository = projectRepository;
        this.tagService = tagService;
    }

    @Override
    @Transactional
    public Project createProject(ProjectCreateDTO projectCreateDTO) {
        val existingProjectWithTitle = projectRepository.findByTitle(projectCreateDTO.getTitle());
        if (existingProjectWithTitle.isEmpty()
                || !existingProjectWithTitle.get().getOwner().equals(projectCreateDTO.getOwner())) {

            List<Tag> tags = tagService.findOrCreateTag(projectCreateDTO.getTags());

            Project project =
                    Project.builder()
                            .title(projectCreateDTO.getTitle())
                            .description(projectCreateDTO.getDescription())
                            .links(projectCreateDTO.getLinks())
                            .tags(tags)
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

        List<Tag> tags = tagService.findOrCreateTag(projectUpdateDTO.getTags());

        val updatedProject = projectToModifyOptional.get();
        updatedProject.setTitle(projectUpdateDTO.getTitle());
        updatedProject.setLinks(projectUpdateDTO.getLinks());
        updatedProject.setTags(tags);
        updatedProject.setDescription(projectUpdateDTO.getDescription());

        return projectRepository.save(updatedProject);
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
    public List<Project> getProjectsByTitleSearch(String pattern) {
        List<Project> projectsStartingWithPattern =
                projectRepository.findByTitleStartsWithIgnoreCaseOrderByTitleAsc(pattern);
        val projectsStartingNames =
                projectsStartingWithPattern.stream()
                        .map(Project::getTitle)
                        .collect(Collectors.toList());

        List<Project> projectsContainingPattern =
                projectRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(pattern).stream()
                        .filter(
                                p ->
                                        projectsStartingNames.stream()
                                                .noneMatch(s -> s.equals(p.getTitle())))
                        .collect(Collectors.toList());

        projectsStartingWithPattern.addAll(projectsContainingPattern);

        return projectsStartingWithPattern;
    }
}
