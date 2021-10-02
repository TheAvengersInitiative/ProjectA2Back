package com.a2.backend.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.a2.backend.BackendApplication;
import com.a2.backend.entity.ForumTag;
import java.util.List;
import java.util.Locale;
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
class ForumTagRepositoryTest {

    @Autowired private ForumTagRepository forumTagRepository;

    ForumTag tag1 = ForumTag.builder().name("tag1").build();

    @Test
    void Test001_TagRepositoryShouldSaveTags() {

        assertTrue(forumTagRepository.findAll().isEmpty());

        assertNull(tag1.getId());
        assertEquals("tag1", tag1.getName());

        forumTagRepository.save(tag1);

        assertFalse(forumTagRepository.findAll().isEmpty());

        List<ForumTag> tags = forumTagRepository.findAll();

        assertEquals(1, tags.size());

        val savedTag = tags.get(0);

        assertNotNull(savedTag.getId());
        assertEquals("tag1", savedTag.getName());
    }

    @Test
    void Test002_TagRepositoryWhenGivenTagNameShouldReturnTagWithThatName() {

        forumTagRepository.save(tag1);

        assertTrue(forumTagRepository.findByName("tag1").isPresent());
    }

    @Test
    void Test003_TagRepositoryWhenGivenNonExistingTagNameShouldNotReturnTag() {

        forumTagRepository.save(tag1);

        assertTrue(forumTagRepository.findByName("tag2").isEmpty());
    }

    @Test
    void Test004_LanguageRepositoryWhenGivenExistingNameFindsLanguage() {
        ForumTag tag1 = ForumTag.builder().name("tAg1").build();
        ForumTag tag2 = ForumTag.builder().name("NotFoundTag").build();
        forumTagRepository.save(tag1);
        forumTagRepository.save(tag2);

        assertEquals(
                forumTagRepository.findForumTagName("aG1".toUpperCase(Locale.ROOT)).get(0), "tAg1");
        assertEquals(forumTagRepository.findForumTagName("ag1".toUpperCase(Locale.ROOT)).size(), 1);
    }
}
