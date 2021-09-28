package com.a2.backend;

import com.a2.backend.annotation.Generated;
import com.a2.backend.entity.Language;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.Tag;
import com.a2.backend.entity.User;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("DemoRunner")
@Transactional
@Generated
public class DemoRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DemoRunner.class);

    @Autowired private Environment env;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;

    public DemoRunner() {}

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
        loadUsers();
        loadProjects();
        logger.info("Created demo data");
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    private void loadUsers() {
        User agustin =
                User.builder()
                        .nickname("Peltevis")
                        .email("agustin.ayerza@ing.austral.edu.ar")
                        .password("password")
                        .confirmationToken("token001")
                        .isActive(true)
                        .build();
        User rodrigo =
                User.builder()
                        .nickname("ropa1998")
                        .email("rodrigo.pazos@ing.austral.edu.ar")
                        .biography(
                                "Backend software engineer, passionate about design and clean code. Working with Java, Python and Scala. ")
                        .password("password")
                        .confirmationToken("token002")
                        .isActive(true)
                        .build();
        User fabrizio =
                User.builder()
                        .nickname("FabriDS23")
                        .email("fabrizio.disanto@ing.austral.edu.ar")
                        .biography(
                                "Software Engineer student, currently working as a full-stack developer. ")
                        .password("password")
                        .confirmationToken("token003")
                        .build();
        userRepository.save(agustin);
        userRepository.save(rodrigo);
        userRepository.save(fabrizio);
    }

    private void loadProjects() {
        Project linux =
                Project.builder()
                        .title("GNU/Linux")
                        .description(
                                "GNU is an extensive collection of free software, which can be used as an operating system or can be used in parts with other operating systems. ")
                        .links(listOf("https://www.gnu.org/", "https://www.linux.org/"))
                        .tags(
                                listOf(
                                        Tag.builder().name("C++").build(),
                                        Tag.builder().name("GNU").build(),
                                        Tag.builder().name("Linux").build()))
                        .owner(userRepository.findByNickname("Peltevis").get())
                        .languages(listOf(Language.builder().name("C++").build()))
                        .build();
        Project tensorFlow =
                Project.builder()
                        .title("TensorFlow")
                        .description(
                                "TensorFlow is a free and open-source software library for machine learning. It can be used across a range of tasks but has a particular focus on training and inference of deep neural networks.")
                        .links(listOf("https://www.tensorflow.org/"))
                        .tags(
                                listOf(
                                        Tag.builder().name("Python").build(),
                                        Tag.builder().name("ML").build(),
                                        Tag.builder().name("CUDA").build()))
                        .owner(userRepository.findByNickname("ropa1998").get())
                        .languages(
                                listOf(
                                        Language.builder().name("Python").build(),
                                        Language.builder().name("ML").build()))
                        .build();
        Project node =
                Project.builder()
                        .title("Node.js")
                        .description(
                                "Node.js is an open-source, cross-platform, JavaScript runtime environment. It executes JavaScript code outside of a browser.")
                        .links(listOf("https://nodejs.org/"))
                        .tags(
                                listOf(
                                        Tag.builder().name("V8").build(),
                                        Tag.builder().name("Node").build()))
                        .owner(userRepository.findByNickname("FabriDS23").get())
                        .languages(
                                listOf(
                                        Language.builder().name("Node").build(),
                                        Language.builder().name("V8").build()))
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
