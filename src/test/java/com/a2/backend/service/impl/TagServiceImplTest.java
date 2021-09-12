package com.a2.backend.service.impl;

import com.a2.backend.entity.Tag;
import com.a2.backend.repository.TagRepository;
import com.a2.backend.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TagServiceImplTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @Test
    void Test001_TagServiceWhenReceivesValidTagNameShouldCreateTagWithGivenName() {
        Tag tag = tagService.createTag("Tag name");

        assertNull(tag.getId());
        assertEquals("Tag name", tag.getName());
    }

    @Test
    void Test002_TagServiceWhenReceivesValidTagNamesShouldCreateTagListWithGivenNames() {
        List<String> tagNames = Arrays.asList("tag1", "tag2");

        List<Tag> tagList = tagService.findOrCreateTag(tagNames);

        assertEquals(2, tagList.size());

        assertEquals("tag1", tagList.get(0).getName());
        assertEquals("tag2", tagList.get(1).getName());
    }

    @Test
    void Test003_TagServiceGivenAnExistingTagWhenTheSameTagIsSentTheOldTagIsReused() {

        Tag tag1 = tagRepository.save(tagService.createTag("tag1"));

        List<Tag> tagList = tagService.findOrCreateTag(Arrays.asList("tag1", "tag2"));

        assertEquals(2, tagList.size());

        assertEquals(tagList.get(0).getId(), tag1.getId());
    }

    @Test
    void Test004_TagServiceWhenReceivesValidTagNameShouldReturnTagWithGivenName() {

        Tag tag1 = tagRepository.save(tagService.createTag("tag1"));
        Tag tag2 = tagRepository.save(tagService.createTag("tag2"));

        List<Tag> tags = tagService.findTags(Arrays.asList("tag1", "tag2"));

        assertEquals(2, tags.size());
        assertEquals(tag1, tags.get(0));
        assertEquals(tag2, tags.get(1));
    }
}
