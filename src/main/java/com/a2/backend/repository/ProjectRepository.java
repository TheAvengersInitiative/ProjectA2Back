package com.a2.backend.repository;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findByTitle(String title);

    void deleteByOwner(User owner);

    @Query("SELECT DISTINCT p FROM Project p JOIN p.tags t WHERE UPPER(t.name) = ?1 ")
    List<Project> findProjectsByTagName(String name);

    @Query("SELECT DISTINCT p FROM Project p JOIN p.languages l WHERE UPPER(l.name) = ?1 ")
    List<Project> findProjectsByLanguageName(String name);

    List<Project> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT DISTINCT p FROM Project p WHERE p.featured=?1 AND UPPER(p.title) LIKE %?2% ")
    List<Project> findByTitleContainingIgnoreCaseAndFeatured(Boolean featured, String title);

    @Query("SELECT DISTINCT p FROM Project p WHERE p.featured=?1")
    List<Project> findAllByFeaturedIsTrue(Boolean featured);

    List<Project> findByOwner(User user);

    List<Project> findByCollaboratorsContaining(User user);

    @Query(
            "SELECT DISTINCT p FROM Project p JOIN p.languages l WHERE p.featured=?1 AND UPPER(l.name) IN ?2 ")
    List<Project> findProjectsByLanguagesInAndFeatured(
            Boolean featured, Collection<String> languages);

    @Query(
            "SELECT DISTINCT p FROM Project p JOIN p.tags t WHERE p.featured=?1 AND UPPER(t.name) IN ?2 ")
    List<Project> findProjectsByTagsInAndFeatured(Boolean featured, Collection<String> tags);

    @Query(
            "SELECT DISTINCT p FROM Project p JOIN p.tags t WHERE p.featured=?1 AND UPPER(p.title) LIKE %?2% AND UPPER(t.name) IN ?3 ")
    List<Project> findProjectsByTagsInAndTitleAndFeatured(
            Boolean featured, String title, Collection<String> tags);

    @Query(
            "SELECT DISTINCT p FROM Project p JOIN p.languages l WHERE p.featured=?1 AND UPPER(p.title) LIKE %?2% AND UPPER(l.name) IN ?3 ")
    List<Project> findProjectsByLanguagesInAndTitleAndFeatured(
            Boolean featured, String title, Collection<String> languages);

    @Query("SELECT DISTINCT p FROM Project p JOIN p.languages l WHERE UPPER(l.name) IN ?1 ")
    List<Project> findProjectsByLanguagesIn(Collection<String> languages);

    @Query("SELECT DISTINCT p FROM Project p JOIN p.tags t WHERE UPPER(t.name) IN ?1 ")
    List<Project> findProjectsByTagsIn(Collection<String> tags);

    @Query(
            "SELECT DISTINCT p FROM Project p JOIN p.tags t WHERE UPPER(p.title) LIKE %?1% AND UPPER(t.name) IN ?2 ")
    List<Project> findProjectsByTagsInAndTitle(String title, Collection<String> tags);

    @Query(
            "SELECT DISTINCT p FROM Project p JOIN p.languages l WHERE UPPER(p.title) LIKE %?1% AND UPPER(l.name) IN ?2 ")
    List<Project> findProjectsByLanguagesInAndTitle(String title, Collection<String> languages);
}
