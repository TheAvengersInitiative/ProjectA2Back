package com.a2.backend.service;

import com.a2.backend.entity.Project;
import com.a2.backend.model.ProjectCreateDTO;

public interface ProjectService{

    Project createProject(ProjectCreateDTO projectCreateDTO);

}
