package com.a2.backend.service;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    Project createProject(ProjectCreateDTO projectCreateDTO);

    List<Project> getAllProjects();

    Project updateProject(ProjectUpdateDTO updateProject, UUID projectToBeUpdatedID);

    void deleteProject(UUID uuid);

    Project getProjectDetails(UUID id);

    void deleteProjectsFromUser(User owner);

    List<Project> getProjectsByTitleSearch(String pattern, int pageNo);
}
