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
                                        Tag.builder().name("C").build(),
                                        Tag.builder().name("C++").build(),
                                        Tag.builder().name("GNU").build(),
                                        Tag.builder().name("Linux").build()))
                        .owner(userRepository.findByNickname("Peltevis").get())
                        .languages(
                                listOf(
                                        Language.builder().name("C").build(),
                                        Language.builder().name("C++").build()))
                        .collaborators(List.of())
                        .applicants(List.of())
                        .rejectedApplicants(
                                List.of(userRepository.findByNickname("ropa1998").get()))
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
                                        Tag.builder().name("MATLAB").build(),
                                        Tag.builder().name("CUDA").build(),
                                        Tag.builder().name("C").build()))
                        .owner(userRepository.findByNickname("ropa1998").get())
                        .languages(
                                listOf(
                                        Language.builder().name("Python").build(),
                                        Language.builder().name("MATLAB").build()))
                                        Language.builder().name("ML").build()))
                        .collaborators(List.of())
                        .applicants(List.of(userRepository.findByNickname("Peltevis").get()))
                        .rejectedApplicants(List.of())
                        .build();
        Project node =
                Project.builder()
                        .title("Node.js")
                        .description(
                                "Node.js is an open-source, cross-platform, JavaScript runtime environment. It executes JavaScript code outside of a browser.")
                        .links(listOf("https://nodejs.org/"))
                        .tags(
                                listOf(
                                        Tag.builder().name("JavaScript").build(),
                                        Tag.builder().name("V8").build(),
                                        Tag.builder().name("Node").build()))
                        .owner(userRepository.findByNickname("FabriDS23").get())
                        .languages(
                                listOf(
                                        Language.builder().name("JavaScript").build(),
                                        Language.builder().name("Node").build(),
                                        Language.builder().name("V8").build()))
                        .collaborators(
                                List.of(
                                        userRepository.findByNickname("ropa1998").get(),
                                        userRepository.findByNickname("Peltevis").get()))
                        .applicants(List.of())
                        .rejectedApplicants(List.of())
                        .build();

        Project geany =
                Project.builder()
                        .title("Geany")
                        .description(
                                "Geany is a small and lightweight IDE that runs on Linux, Windows, MacOS, and every platform that is supported by GTK libraries.")
                        .links(listOf("https://www.geany.org/"))
                        .tags(
                                listOf(
                                        Tag.builder().name("IDE").build(),
                                        Tag.builder().name("C").build(),
                                        Tag.builder().name("Geany").build(),
                                        Tag.builder().name("C++").build()))
                        .owner(userRepository.findByNickname("FabriDS23").get())
                        .languages(
                                listOf(
                                        Language.builder().name("C").build(),
                                        Language.builder().name("C++").build()))
                        .build();
        Project django =
                Project.builder()
                        .title("Django")
                        .description(
                                "Django is a high-level Python Web framework, and it’s very loveable. For one thing, it’s designed to help developers achieve their most important objective: rapid development.")
                        .links(listOf("https://www.djangoproject.com/"))
                        .tags(
                                listOf(
                                        Tag.builder().name("Python").build(),
                                        Tag.builder().name("framework").build(),
                                        Tag.builder().name("web").build()))
                        .owner(userRepository.findByNickname("Peltevis").get())
                        .languages(listOf(Language.builder().name("Python").build()))
                        .build();
        Project sakai =
                Project.builder()
                        .title("Sakai")
                        .description(
                                "Sakai is a free, community source, educational software platform designed to support teaching, research and collaboration.")
                        .links(listOf("https://www.sakailms.org/"))
                        .tags(listOf(Tag.builder().name("Java").build()))
                        .owner(userRepository.findByNickname("Peltevis").get())
                        .languages(listOf(Language.builder().name("Java").build()))
                        .build();
        Project apache =
                Project.builder()
                        .title("ApacheCassandra")
                        .description(
                                "Apache Cassandra is a distributed and decentralized database designed to manage massive amounts of structured and unstructured data across the world.")
                        .links(listOf("https://cassandra.apache.org/"))
                        .tags(
                                listOf(
                                        Tag.builder().name("Java").build(),
                                        Tag.builder().name("Apache").build()))
                        .owner(userRepository.findByNickname("FabriDS23").get())
                        .languages(listOf(Language.builder().name("Java").build()))
                        .featured(true)
                        .build();
        Project renovate =
                Project.builder()
                        .title("Renovate")
                        .description(
                                "Renovate is the essential “keep absolutely everything up-to-date” code maintenance tool.")
                        .links(listOf("https://www.whitesourcesoftware.com/"))
                        .tags(
                                listOf(
                                        Tag.builder().name("JavaScript").build(),
                                        Tag.builder().name("TypeScript").build()))
                        .owner(userRepository.findByNickname("ropa1998").get())
                        .languages(
                                listOf(
                                        Language.builder().name("JavaSript").build(),
                                        Language.builder().name("TypeScript").build()))
                        .featured(true)
                        .build();
        Project kubernetes =
                Project.builder()
                        .title("Kubernetes")
                        .description(
                                "Kubernetes, also known as K8s, is an open-source system for automating deployment, scaling, and management of containerized applications.")
                        .links(listOf("https://kubernetes.io/"))
                        .tags(
                                listOf(
                                        Tag.builder().name("Go").build(),
                                        Tag.builder().name("K8s").build(),
                                        Tag.builder().name("Kubernetes").build()))
                        .owner(userRepository.findByNickname("ropa1998").get())
                        .languages(listOf(Language.builder().name("Go").build()))
                        .featured(true)
                        .build();
        Project ansible =
                Project.builder()
                        .title("RedHatAnsible")
                        .description(
                                "Ansible is an IT automation tool that “loves the repetitive work your people hate.” ")
                        .links(listOf("https://www.ansible.com/"))
                        .tags(
                                listOf(
                                        Tag.builder().name("RedHat").build(),
                                        Tag.builder().name("Ansible").build(),
                                        Tag.builder().name("Python").build()))
                        .owner(userRepository.findByNickname("ropa1998").get())
                        .languages(listOf(Language.builder().name("Python").build()))
                        .featured(true)
                        .build();

        projectRepository.save(linux);
        projectRepository.save(tensorFlow);
        projectRepository.save(node);
        projectRepository.save(geany);
        projectRepository.save(django);
        projectRepository.save(sakai);
        projectRepository.save(apache);
        projectRepository.save(renovate);
        projectRepository.save(kubernetes);
        projectRepository.save(ansible);
    }

    @SafeVarargs
    private static <T> List<T> listOf(T... args) {
        return Arrays.stream(args).collect(Collectors.toList());
    }
}
