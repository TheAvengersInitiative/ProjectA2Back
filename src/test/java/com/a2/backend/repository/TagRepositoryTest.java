package com.a2.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.BackendApplication;
import com.a2.backend.entity.Tag;
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
class TagRepositoryTest {

    @Autowired private TagRepository tagRepository;

    Tag tag1 = Tag.builder().name("tag1").build();

    @Test
    void Test001_TagRepositoryShouldSaveTags() {

        assertTrue(tagRepository.findAll().isEmpty());

        assertNull(tag1.getId());
        assertEquals("tag1", tag1.getName());

        tagRepository.save(tag1);

        assertFalse(tagRepository.findAll().isEmpty());

        List<Tag> tags = tagRepository.findAll();

        assertEquals(1, tags.size());

        val savedTag = tags.get(0);

        assertNotNull(savedTag.getId());
        assertEquals("tag1", savedTag.getName());
    }

    @Test
    void Test002_TagRepositoryWhenGivenTagNameShouldReturnTagWithThatName() {

        tagRepository.save(tag1);

        assertTrue(tagRepository.findByName("tag1").isPresent());
    }

    @Test
    void Test003_TagRepositoryWhenGivenNonExistingTagNameShouldNotReturnTag() {

        tagRepository.save(tag1);

        assertTrue(tagRepository.findByName("tag2").isEmpty());
    }
}
