package com.a2.backend.service;

import com.a2.backend.entity.Project;
import com.a2.backend.model.ProjectCreateDTO;

import java.util.List;

public interface ProjectService{

    Project createProject(ProjectCreateDTO projectCreateDTO);

    List<Project> getAllProjects();

}
