package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.a2.backend.entity.ForumTag;
import com.a2.backend.repository.ForumTagRepository;
import com.a2.backend.service.ForumTagService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ForumTagServiceImplTest {
    @Autowired private ForumTagService forumTagService;

    @Autowired private ForumTagRepository forumTagRepository;

    @Test
    void Test001_ForumTagServiceWhenReceivesValidTagNameShouldCreateTagWithGivenName() {
        ForumTag tag = forumTagService.createTag("Tag name");

        assertNull(tag.getId());
        assertEquals("Tag name", tag.getName());
    }

    @Test
    void Test002_ForumTagServiceWhenReceivesValidTagNamesShouldCreateTagListWithGivenNames() {
        List<String> tagNames = Arrays.asList("tag1", "tag2");

        List<ForumTag> tagList = forumTagService.createTag(tagNames);

        assertEquals(2, tagList.size());

        assertEquals("tag1", tagList.get(0).getName());
        assertEquals("tag2", tagList.get(1).getName());
    }

    @Test
    void Test003_ForumTagServiceGivenAnExistingTagWhenTheSameTagIsSentTheOldTagIsReused() {

        ForumTag tag1 = forumTagRepository.save(forumTagService.createTag("tag1"));

        List<ForumTag> tagList = forumTagService.createTag(Arrays.asList("tag1", "tag2"));

        assertEquals(2, tagList.size());

        assertEquals(tagList.get(0).getId(), tag1.getId());
    }

    @Test
    void Test004_ForumTagServiceWhenReceivesValidTagNameShouldReturnTagWithGivenName() {

        ForumTag tag1 = forumTagRepository.save(forumTagService.createTag("tag1"));
        ForumTag tag2 = forumTagRepository.save(forumTagService.createTag("tag2"));

        List<ForumTag> tags = forumTagService.findTagsByNames(Arrays.asList("tag1", "tag2"));

        assertEquals(2, tags.size());
        assertEquals(tag1, tags.get(0));
        assertEquals(tag2, tags.get(1));
    }

    @Test
    void Test005_ForumTagServiceGivenValidTagNamesToUpdateAndCurrentTagsShouldReturnRemovedTags() {

        ForumTag tag1 = forumTagRepository.save(forumTagService.createTag("tag1"));
        ForumTag tag2 = forumTagRepository.save(forumTagService.createTag("tag2"));

        List<ForumTag> tagList = Arrays.asList(tag1, tag2);
        List<String> updatedTagNames = Arrays.asList("tag1", "tag3");

        List<ForumTag> removedTags = forumTagService.getRemovedTags(updatedTagNames, tagList);

        assertEquals(1, removedTags.size());
        assertEquals("tag2", removedTags.get(0).getName());
    }
}
