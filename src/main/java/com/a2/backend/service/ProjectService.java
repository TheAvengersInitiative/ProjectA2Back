package com.a2.backend.service;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.model.*;
import java.util.List;
import java.util.UUID;

public interface ProjectService {

    Project createProject(ProjectCreateDTO projectCreateDTO);

    List<ProjectDTO> getAllProjects();

    ProjectDTO updateProject(ProjectUpdateDTO updateProject, UUID projectToBeUpdatedID);

    void deleteProject(UUID uuid);

    ProjectDTO getProjectDetails(UUID id);

    void deleteProjectsFromUser(User owner);

    List<String> getValidLanguageNames();

    List<ProjectDTO> searchProjectsByFilter(ProjectSearchDTO projectSearchDTO);

    List<ProjectDTO> getFeaturedProject();

    List<ProjectDTO> getMyProjects();

    ProjectDTO applyToProject(UUID projectToApplyID);

    List<ProjectDTO> getProjectsByOwner(User owner);

    List<ProjectDTO> getCollaboratingProjects(User user);

    List<ProjectUserDTO> getProjectApplicants(UUID uuid);

    List<ProjectUserDTO> acceptApplicant(UUID projectId, UUID userId);

    List<ProjectUserDTO> rejectApplicant(UUID projectId, UUID userId);
}
