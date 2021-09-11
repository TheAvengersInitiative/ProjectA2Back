package com.a2.backend;

import com.a2.backend.annotation.Generated;
import com.a2.backend.entity.Project;
import com.a2.backend.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component("DemoRunner")
@Transactional
@Generated
public class DemoRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DemoRunner.class);

    @Autowired
    private Environment env;
    @Autowired
    private ProjectRepository projectRepository;

    public DemoRunner() {
    }

    @Override
    public void run(String... args) {
        for (String profile : env.getActiveProfiles()) {
            if (profile.equalsIgnoreCase("local")) {
                createDemoData();
                return;
            }
        }
        logger.info("Profile: \"local\" not found. Didn't create demo data");
    }

    private void createDemoData() {
        loadProjects();
        logger.info("Created demo data");
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    private void loadProjects() {
        Project linux =
                Project.builder()
                        .title("GNU/Linux")
                        .description(
                                "GNU is an extensive collection of free software, which can be used as an operating system or can be used in parts with other operating systems. ")
                        .links(listOf("https://www.gnu.org/", "https://www.linux.org/"))
                        // .tags(listOf("C", "C++", "GNU", "Linux"))
                        .build();
        Project tensorFlow =
                Project.builder()
                        .title("TensorFlow")
                        .description(
                                "TensorFlow is a free and open-source software library for machine learning. It can be used across a range of tasks but has a particular focus on training and inference of deep neural networks.")
                        .links(listOf("https://www.tensorflow.org/"))
                        // .tags(listOf("Python", "ML", "CUDA", "C"))
                        .build();
        Project node =
                Project.builder()
                        .title("Node.js")
                        .description(
                                "Node.js is an open-source, cross-platform, JavaScript runtime environment. It executes JavaScript code outside of a browser.")
                        .links(listOf("https://nodejs.org/"))
                        // .tags(listOf("JavaScript", "V8", "Node"))
                        .build();
        projectRepository.save(linux);
        projectRepository.save(tensorFlow);
        projectRepository.save(node);
    }

    @SafeVarargs
    private static <T> List<T> listOf(T... args) {
        return Arrays.stream(args).collect(Collectors.toList());
    }
}
