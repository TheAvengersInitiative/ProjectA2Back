package com.a2.backend.service;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.model.*;
import java.util.List;
import java.util.UUID;

public interface ProjectService {

    Project createProject(ProjectCreateDTO projectCreateDTO);

    List<Project> getAllProjects();

    Project updateProject(ProjectUpdateDTO updateProject, UUID projectToBeUpdatedID);

    void deleteProject(UUID uuid);

    Project getProjectDetails(UUID id);

    void deleteProjectsFromUser(User owner);

    List<String> getValidLanguageNames();

    List<Project> searchProjectsByFilter(ProjectSearchDTO projectSearchDTO);

    List<Project> getFeaturedProject();

    List<Project> getMyProjects();

    ProjectDTO applyToProject(UUID projectToApplyID);

    List<Project> getProjectsByOwner(User owner);

    List<Project> getCollaboratingProjects(User user);

    List<ProjectUserDTO> getProjectApplicants(UUID uuid);

    List<ProjectUserDTO> acceptApplicant(UUID projectId, UUID userId);

    List<ProjectUserDTO> rejectApplicant(UUID projectId, UUID userId);
}
