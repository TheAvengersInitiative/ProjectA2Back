package com.a2.backend.service.impl;

import com.a2.backend.entity.Project;
import com.a2.backend.exception.ProjectNotFoundException;
import com.a2.backend.exception.ProjectWithThatIdDoesntExistException;
import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.ProjectService;
import java.util.List;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project createProject(ProjectCreateDTO projectCreateDTO) {
        // TODO: verify owner is user that created the project
        val existingProjectWithTitle = projectRepository.findByTitle(projectCreateDTO.getTitle());
        if (existingProjectWithTitle.isEmpty()) {
            Project project =
                    Project.builder()
                            .title(projectCreateDTO.getTitle())
                            .description(projectCreateDTO.getDescription())
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
    public Project updateProject(ProjectUpdateDTO projectUpdateDTO, String projectToBeUpdatedID) {
        val projectToModifyOptional = projectRepository.findById(projectToBeUpdatedID);
        if (projectToModifyOptional.isEmpty()) {
            throw new ProjectNotFoundException(
                    String.format(
                            "The project with that id: %s does not exist!", projectToBeUpdatedID));
        }
        val updatedProject = projectToModifyOptional.get();
        updatedProject.setTitle(projectUpdateDTO.getTitle());
        updatedProject.setLinks(projectUpdateDTO.getLinks());
        updatedProject.setTags(projectUpdateDTO.getTags());
        updatedProject.setDescription(projectUpdateDTO.getDescription());

        return projectRepository.save(updatedProject);
    }

    @Override
    public void deleteProject(String uuid) {
        if (projectRepository.existsById(uuid)) {
            projectRepository.deleteById(uuid);
            return;
        }
        throw new ProjectWithThatIdDoesntExistException(
                String.format("No project found for id: %s", uuid));
    }

    @Override
    public Project getProjectDetails(String projectID) {
        return projectRepository
                .findById(projectID)
                .orElseThrow(
                        () ->
                                new ProjectWithThatIdDoesntExistException(
                                        String.format("No project found for id: %s", projectID)));
    }
}
