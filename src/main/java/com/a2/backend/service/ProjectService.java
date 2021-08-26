package com.a2.backend.service;

import com.a2.backend.entity.Project;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;

import java.util.List;

public interface ProjectService{

    Project createProject(ProjectCreateDTO projectCreateDTO);

    List<Project> getAllProjects();

    Project updateProject(ProjectUpdateDTO updateProject , String projectToBeUpdatedID);

}
