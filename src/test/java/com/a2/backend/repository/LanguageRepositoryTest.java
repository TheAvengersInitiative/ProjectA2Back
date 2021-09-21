package com.a2.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.BackendApplication;
import com.a2.backend.entity.Language;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@AutoConfigureWebClient
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({BackendApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class LanguageRepositoryTest {

    @Autowired private LanguageRepository languageRepository;

    Language language = Language.builder().name("Java").build();

    @Test
    void Test001_LanguageRepositoryShouldSaveLanguage() {

        assertTrue(languageRepository.findAll().isEmpty());

        assertNull(language.getId());
        assertEquals("Java", language.getName());

        languageRepository.save(language);

        assertFalse(languageRepository.findAll().isEmpty());

        List<Language> languages = languageRepository.findAll();

        assertEquals(1, languages.size());

        val savedLanguage = languages.get(0);

        assertNotNull(savedLanguage.getId());
        assertEquals("Java", savedLanguage.getName());
    }

    @Test
    void Test002_LanguageRepositoryWhenGivenLanguageNameShouldReturnLanguageWithThatName() {

        languageRepository.save(language);

        assertTrue(languageRepository.findByName("Java").isPresent());
    }

    @Test
    void Test003_LanguageRepositoryWhenGivenNonExistingLanguageNameShouldNotReturnLanguage() {

        languageRepository.save(language);

        assertTrue(languageRepository.findByName("Python").isEmpty());
    }
}
