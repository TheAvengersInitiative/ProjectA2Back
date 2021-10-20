package com.a2.backend;

import com.a2.backend.annotation.Generated;
import com.a2.backend.constants.PrivacyConstant;
import com.a2.backend.entity.*;
import com.a2.backend.repository.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired private LanguageRepository languageRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private ForumTagRepository forumTagRepository;
    @Autowired private PasswordEncoder passwordEncoder;

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
        loadLanguages();
        loadTags();
        loadUsers();
        loadForumTags();
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
                        .password(passwordEncoder.encode("password"))
                        .preferredTags(List.of("GNU", "MATLAB"))
                        .reputation(4)
                        .confirmationToken("token001")
                        .collaboratedProjectsPrivacy(PrivacyConstant.PRIVATE)
                        .languagesPrivacy(PrivacyConstant.PRIVATE)
                        .isActive(true)
                        .build();
        User rodrigo =
                User.builder()
                        .nickname("ropa1998")
                        .email("rodrigo.pazos@ing.austral.edu.ar")
                        .biography(
                                "Backend software engineer, passionate about design and clean code. Working with Java, Python and Scala. ")
                        .password(passwordEncoder.encode("password"))
                        .reputation(2.5)
                        .confirmationToken("token002")
                        .isActive(true)
                        .build();
        User fabrizio =
                User.builder()
                        .nickname("FabriDS23")
                        .email("fabrizio.disanto@ing.austral.edu.ar")
                        .biography(
                                "Software Engineer student, currently working as a full-stack developer. ")
                        .password(passwordEncoder.encode("password"))
                        .confirmationToken("token003")
                        .languagesPrivacy(PrivacyConstant.PRIVATE)
                        .collaboratedProjectsPrivacy(PrivacyConstant.PRIVATE)
                        .ownedProjectsPrivacy(PrivacyConstant.PRIVATE)
                        .tagsPrivacy(PrivacyConstant.PRIVATE)
                        .build();
        User franz =
                User.builder()
                        .nickname("Franz")
                        .email("franz.sotoleal@ing.austral.edu.ar")
                        .password(passwordEncoder.encode("password"))
                        .confirmationToken("token004")
                        .preferredTags(List.of("AI", "Open-source"))
                        .preferredLanguages(List.of("Go", "Rust", "C++"))
                        .languagesPrivacy(PrivacyConstant.PRIVATE)
                        .collaboratedProjectsPrivacy(PrivacyConstant.PRIVATE)
                        .ownedProjectsPrivacy(PrivacyConstant.PRIVATE)
                        .tagsPrivacy(PrivacyConstant.PRIVATE)
                        .isActive(true)
                        .build();
        userRepository.save(agustin);
        userRepository.save(rodrigo);
        userRepository.save(fabrizio);
        userRepository.save(franz);
    }

    private void loadProjects() {
        User peltevis = userRepository.findByNickname("Peltevis").get();
        User fabriDS23 = userRepository.findByNickname("FabriDS23").get();
        User ropa1998 = userRepository.findByNickname("ropa1998").get();
        User franz = userRepository.findByNickname("Franz").get();

        Project linux =
                Project.builder()
                        .title("GNU/Linux")
                        .description(
                                "GNU is an extensive collection of free software, which can be used as an operating system or can be used in parts with other operating systems. ")
                        .links(listOf("https://www.gnu.org/", "https://www.linux.org/"))
                        .tags(
                                listOf(
                                        tagRepository.findByName("GNU").get(),
                                        tagRepository.findByName("linux").get()))
                        .owner(peltevis)
                        .languages(
                                listOf(
                                        languageRepository.findByName("C").get(),
                                        languageRepository.findByName("C++").get()))
                        .forumTags(
                                listOf(
                                        forumTagRepository.findByName("help").get(),
                                        forumTagRepository.findByName("help please").get()))
                        .collaborators(List.of())
                        .applicants(List.of())
                        .reviews(List.of())
                        .build();
        Project tensorFlow =
                Project.builder()
                        .title("TensorFlow")
                        .description(
                                "TensorFlow is a free and open-source software library for machine learning. It can be used across a range of tasks but has a particular focus on training and inference of deep neural networks.")
                        .links(listOf("https://www.tensorflow.org/"))
                        .tags(
                                listOf(
                                        tagRepository.findByName("AI").get(),
                                        tagRepository.findByName("Machine Learning").get()))
                        .owner(ropa1998)
                        .languages(
                                listOf(
                                        languageRepository.findByName("Python").get(),
                                        languageRepository.findByName("MatLab").get()))
                        .forumTags(
                                listOf(
                                        forumTagRepository.findByName("Great").get(),
                                        forumTagRepository.findByName("software library").get()))
                        .collaborators(List.of())
                        .applicants(List.of(peltevis))
                        .reviews(List.of())
                        .build();
        Project node =
                Project.builder()
                        .title("Node.js")
                        .description(
                                "Node.js is an open-source, cross-platform, JavaScript runtime environment. It executes JavaScript code outside of a browser.")
                        .links(listOf("https://nodejs.org/"))
                        .tags(
                                listOf(
                                        tagRepository.findByName("Frontend").get(),
                                        tagRepository.findByName("Cross-Platform").get()))
                        .owner(fabriDS23)
                        .languages(
                                listOf(
                                        languageRepository.findByName("JavaScript").get(),
                                        languageRepository.findByName("V8").get(),
                                        languageRepository.findByName("Node").get()))
                        .forumTags(
                                listOf(
                                        forumTagRepository.findByName("help").get(),
                                        forumTagRepository.findByName("help please").get()))
                        .collaborators(List.of(ropa1998, peltevis))
                        .applicants(List.of())
                        .reviews(
                                List.of(
                                        Review.builder()
                                                .collaborator(ropa1998)
                                                .score(5)
                                                .date(LocalDateTime.now())
                                                .comment("Did a great job")
                                                .build(),
                                        Review.builder()
                                                .collaborator(ropa1998)
                                                .score(3)
                                                .date(LocalDateTime.now().plusMinutes(1))
                                                .comment(null)
                                                .build(),
                                        Review.builder()
                                                .collaborator(peltevis)
                                                .score(4)
                                                .date(LocalDateTime.now().plusMinutes(2))
                                                .comment(null)
                                                .build()))
                        .build();

        Project geany =
                Project.builder()
                        .title("Geany")
                        .description(
                                "Geany is a small and lightweight IDE that runs on Linux, Windows, MacOS, and every platform that is supported by GTK libraries.")
                        .links(listOf("https://www.geany.org/"))
                        .tags(
                                listOf(
                                        tagRepository.findByName("IDE").get(),
                                        tagRepository.findByName("linux").get()))
                        .owner(fabriDS23)
                        .languages(
                                listOf(
                                        languageRepository.findByName("C").get(),
                                        languageRepository.findByName("C++").get()))
                        .forumTags(listOf(forumTagRepository.findByName("advice").get()))
                        .collaborators(List.of(peltevis))
                        .applicants(List.of())
                        .reviews(List.of())
                        .build();
        Project django =
                Project.builder()
                        .title("Django")
                        .description(
                                "Django is a high-level Python Web framework, and it’s very loveable. For one thing, it’s designed to help developers achieve their most important objective: rapid development.")
                        .links(listOf("https://www.djangoproject.com/"))
                        .tags(
                                listOf(
                                        tagRepository.findByName("Backend").get(),
                                        tagRepository.findByName("Framework").get()))
                        .owner(peltevis)
                        .languages(listOf(languageRepository.findByName("Python").get()))
                        .forumTags(
                                listOf(
                                        forumTagRepository.findByName("framework").get(),
                                        forumTagRepository.findByName("Great").get()))
                        .collaborators(List.of(ropa1998))
                        .applicants(List.of(fabriDS23))
                        .reviews(
                                List.of(
                                        Review.builder()
                                                .collaborator(ropa1998)
                                                .score(2)
                                                .date(LocalDateTime.now())
                                                .comment(null)
                                                .build()))
                        .build();
        Project sakai =
                Project.builder()
                        .title("Sakai")
                        .description(
                                "Sakai is a free, community source, educational software platform designed to support teaching, research and collaboration.")
                        .links(listOf("https://www.sakailms.org/"))
                        .tags(listOf(tagRepository.findByName("Education").get()))
                        .owner(peltevis)
                        .languages(listOf(languageRepository.findByName("Java").get()))
                        .forumTags(listOf(forumTagRepository.findByName("help please").get()))
                        .collaborators(List.of())
                        .applicants(List.of())
                        .reviews(List.of())
                        .build();
        Project apache =
                Project.builder()
                        .title("ApacheCassandra")
                        .description(
                                "Apache Cassandra is a distributed and decentralized database designed to manage massive amounts of structured and unstructured data across the world.")
                        .links(listOf("https://cassandra.apache.org/"))
                        .tags(
                                listOf(
                                        tagRepository.findByName("Big Data").get(),
                                        tagRepository.findByName("Dependency").get()))
                        .owner(fabriDS23)
                        .languages(listOf(languageRepository.findByName("Java").get()))
                        .forumTags(
                                listOf(
                                        forumTagRepository.findByName("advice").get(),
                                        forumTagRepository.findByName("framework").get()))
                        .featured(true)
                        .collaborators(List.of())
                        .applicants(List.of(ropa1998))
                        .reviews(List.of())
                        .build();
        Project renovate =
                Project.builder()
                        .title("Renovate")
                        .description(
                                "Renovate is the essential “keep absolutely everything up-to-date” code maintenance tool.")
                        .links(listOf("https://www.whitesourcesoftware.com/"))
                        .tags(listOf(tagRepository.findByName("Dependency").get()))
                        .owner(ropa1998)
                        .languages(
                                listOf(
                                        languageRepository.findByName("JavaScript").get(),
                                        languageRepository.findByName("Java").get(),
                                        languageRepository.findByName("TypeScript").get()))
                        .forumTags(listOf(forumTagRepository.findByName("maintenance tool").get()))
                        .featured(true)
                        .collaborators(List.of())
                        .applicants(List.of())
                        .reviews(List.of())
                        .build();
        Project kubernetes =
                Project.builder()
                        .title("Kubernetes")
                        .description(
                                "Kubernetes, also known as K8s, is an open-source system for automating deployment, scaling, and management of containerized applications.")
                        .links(listOf("https://kubernetes.io/"))
                        .tags(listOf(tagRepository.findByName("Automation").get()))
                        .owner(ropa1998)
                        .languages(listOf(languageRepository.findByName("GO").get()))
                        .forumTags(listOf(forumTagRepository.findByName("help").get()))
                        .featured(true)
                        .collaborators(List.of(peltevis, fabriDS23, franz))
                        .applicants(List.of())
                        .reviews(List.of())
                        .build();
        Project ansible =
                Project.builder()
                        .title("RedHatAnsible")
                        .description(
                                "Ansible is an IT automation tool that “loves the repetitive work your people hate.” ")
                        .links(listOf("https://www.ansible.com/"))
                        .tags(
                                listOf(
                                        tagRepository.findByName("Tool").get(),
                                        tagRepository.findByName("Automation").get()))
                        .owner(ropa1998)
                        .languages(listOf(languageRepository.findByName("Python").get()))
                        .forumTags(listOf(forumTagRepository.findByName("help").get()))
                        .featured(true)
                        .collaborators(List.of(peltevis))
                        .applicants(List.of())
                        .reviews(List.of())
                        .build();
        Project flask =
                Project.builder()
                        .title("Flask")
                        .description("This is a micro web framework written in Python. ")
                        .links(listOf("https://flask.palletsprojects.com/"))
                        .tags(listOf(tagRepository.findByName("Tool").get()))
                        .owner(userRepository.findByNickname("ropa1998").get())
                        .languages(listOf(languageRepository.findByName("Python").get()))
                        .forumTags(listOf(forumTagRepository.findByName("help").get()))
                        .collaborators(List.of(userRepository.findByNickname("Peltevis").get()))
                        .applicants(List.of())
                        .reviews(List.of())
                        .build();
        tensorFlow.setDiscussions(
                List.of(
                        Discussion.builder()
                                .title("Bug US-2.1.1")
                                .forumTags(List.of(forumTagRepository.findByName("help").get()))
                                .project(tensorFlow)
                                .comments(List.of())
                                .body(
                                        "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
                                .build()));
        django.setDiscussions(
                List.of(
                        Discussion.builder()
                                .title("Issue with User ViewSet")
                                .forumTags(
                                        List.of(
                                                forumTagRepository.findByName("help").get(),
                                                forumTagRepository.findByName("advice").get()))
                                .project(django)
                                .owner(peltevis)
                                .body(
                                        "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
                                .comments(
                                        List.of(
                                                Comment.builder()
                                                        .comment(
                                                                "A ViewSet class is simply a type of class-based View, that does not provide any method handlers such as .get() or .post(), and instead provides actions such as .list() and .create().")
                                                        .user(peltevis)
                                                        .date(LocalDateTime.now())
                                                        .highlighted(false)
                                                        .hidden(false)
                                                        .build(),
                                                Comment.builder()
                                                        .comment(
                                                                "The method handlers for a ViewSet are only bound to the corresponding actions at the point of finalizing the view, using the .as_view() method.")
                                                        .user(peltevis)
                                                        .date(LocalDateTime.now().plusNanos(1))
                                                        .highlighted(true)
                                                        .hidden(false)
                                                        .build(),
                                                Comment.builder()
                                                        .comment(
                                                                "An advantage of using a ViewSet class over using a View class is that repeated logic can be combined into a single class.")
                                                        .user(peltevis)
                                                        .date(LocalDateTime.now().plusNanos(2))
                                                        .highlighted(false)
                                                        .hidden(true)
                                                        .build(),
                                                Comment.builder()
                                                        .comment(
                                                                "The ViewSet class inherits from APIView. You can use any of the standard attributes such as permission_classes, authentication_classes in order to control the API policy on the viewset.")
                                                        .user(peltevis)
                                                        .date(LocalDateTime.now().plusNanos(3))
                                                        .highlighted(true)
                                                        .hidden(false)
                                                        .build()))
                                .build()));
        kubernetes.setDiscussions(
                List.of(
                        Discussion.builder()
                                .body(
                                        "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
                                .title("Not working on MacOS")
                                .forumTags(List.of(forumTagRepository.findByName("help").get()))
                                .project(kubernetes)
                                .owner(franz)
                                .comments(
                                        List.of(
                                                Comment.builder()
                                                        .comment("You should try a reinstall")
                                                        .user(franz)
                                                        .date(LocalDateTime.now())
                                                        .highlighted(false)
                                                        .hidden(false)
                                                        .build(),
                                                Comment.builder()
                                                        .comment("Or maybe just a reboot first...")
                                                        .user(ropa1998)
                                                        .date(LocalDateTime.now().plusNanos(1))
                                                        .highlighted(false)
                                                        .hidden(true)
                                                        .build()))
                                .build(),
                        Discussion.builder()
                                .body(
                                        "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
                                .title("Bug US-5.4")
                                .forumTags(List.of(forumTagRepository.findByName("advice").get()))
                                .project(kubernetes)
                                .comments(List.of())
                                .build()));

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
        projectRepository.save(flask);
    }

    private void loadTags() {
        Tag tool = Tag.builder().name("Tool").build();
        Tag linux = Tag.builder().name("linux").build();
        Tag ai = Tag.builder().name("AI").build();
        Tag bigData = Tag.builder().name("Big Data").build();
        Tag frontend = Tag.builder().name("Frontend").build();
        Tag backend = Tag.builder().name("Backend").build();
        Tag framework = Tag.builder().name("Framework").build();
        Tag dependency = Tag.builder().name("Dependency").build();
        Tag education = Tag.builder().name("Education").build();
        Tag ide = Tag.builder().name("IDE").build();
        Tag crossplat = Tag.builder().name("Cross-Platform").build();
        Tag machineLearning = Tag.builder().name("Machine Learning").build();
        Tag gnu = Tag.builder().name("GNU").build();
        Tag automation = Tag.builder().name("Automation").build();

        tagRepository.save(tool);
        tagRepository.save(linux);
        tagRepository.save(ai);
        tagRepository.save(bigData);
        tagRepository.save(frontend);
        tagRepository.save(backend);
        tagRepository.save(framework);
        tagRepository.save(dependency);
        tagRepository.save(education);
        tagRepository.save(ide);
        tagRepository.save(crossplat);
        tagRepository.save(machineLearning);
        tagRepository.save(gnu);
        tagRepository.save(automation);
    }

    private void loadLanguages() {
        Language c = Language.builder().name("C").build();
        Language cplusplus = Language.builder().name("C++").build();
        Language python = Language.builder().name("Python").build();
        Language java = Language.builder().name("Java").build();
        Language javascript = Language.builder().name("JavaScript").build();
        Language matlab = Language.builder().name("MatLab").build();
        Language v8 = Language.builder().name("V8").build();
        Language typescript = Language.builder().name("TypeScript").build();
        Language go = Language.builder().name("GO").build();
        Language node = Language.builder().name("Node").build();

        languageRepository.save(c);
        languageRepository.save(cplusplus);
        languageRepository.save(python);
        languageRepository.save(java);
        languageRepository.save(javascript);
        languageRepository.save(v8);
        languageRepository.save(matlab);
        languageRepository.save(typescript);
        languageRepository.save(go);
        languageRepository.save(node);
    }

    private void loadForumTags() {
        ForumTag help = ForumTag.builder().name("help").build();
        ForumTag advice = ForumTag.builder().name("advice").build();
        ForumTag framework = ForumTag.builder().name("framework").build();
        ForumTag maintenanceTool = ForumTag.builder().name("maintenance tool").build();
        ForumTag helpPlease = ForumTag.builder().name("help please").build();
        ForumTag softwareLib = ForumTag.builder().name("software library").build();
        ForumTag great = ForumTag.builder().name("Great").build();

        forumTagRepository.save(help);
        forumTagRepository.save(advice);
        forumTagRepository.save(framework);
        forumTagRepository.save(maintenanceTool);
        forumTagRepository.save(helpPlease);
        forumTagRepository.save(softwareLib);
        forumTagRepository.save(great);
    }

    @SafeVarargs
    private static <T> List<T> listOf(T... args) {
        return Arrays.stream(args).collect(Collectors.toList());
    }
}
