package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.Tag;
import com.a2.backend.service.TagService;
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
class TagServiceImplTest {

    @Autowired private TagService tagService;

    @Test
    void Test001_TagServiceWhenReceivesValidTagNameShouldCreateTagWithGivenName() {
        Tag tag = tagService.createTag("Tag name");

        assertNull(tag.getId());
        assertEquals("Tag name", tag.getName());
    }

    @Test
    void Test002_TagServiceWhenReceivesValidTagNamesShouldCreateTagListWithGivenNames() {
        List<String> tagNames = Arrays.asList("tag1", "tag2");

        List<Tag> tagList = tagService.createTagsList(tagNames);

        assertEquals(2, tagList.size());

        assertEquals("tag1", tagList.get(0).getName());
        assertEquals("tag2", tagList.get(1).getName());
    }
}
