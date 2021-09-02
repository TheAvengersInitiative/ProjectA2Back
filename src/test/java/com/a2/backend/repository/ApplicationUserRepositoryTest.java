package com.a2.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.BackendApplication;
import com.a2.backend.entity.ApplicationUser;
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
class ApplicationUserRepositoryTest {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    String nickname = "nickname";
    String email = "some@email.com";
    String biography = "bio";
    String password = "hashed_password";

    ApplicationUser applicationUser =
            ApplicationUser.builder()
                    .nickname(nickname)
                    .email(email)
                    .biography(biography)
                    .password(password)
                    .build();

    @Test
    void Test001_GivenAUserWhenSavingItThenItIsSaved() {

        assertTrue(applicationUserRepository.findAll().isEmpty());

        applicationUserRepository.save(applicationUser);

        val users = applicationUserRepository.findAll();

        assertEquals(1, users.size());

        ApplicationUser persistedApplicationUser = users.get(0);

        assertNotNull(persistedApplicationUser.getId());
        assertEquals(nickname, persistedApplicationUser.getNickname());
        assertEquals(email, persistedApplicationUser.getEmail());
        assertEquals(biography, persistedApplicationUser.getBiography());
        assertEquals(password, persistedApplicationUser.getPassword());
        assertFalse(persistedApplicationUser.isActive());
    }

    @Test
    void Test002_GivenAPersistedUserWhenFindingByNicknameThenItIsReturned() {
        applicationUserRepository.save(applicationUser);


        val optionalPersistedUser = applicationUserRepository.findByNickname("nickname");
        assertTrue(optionalPersistedUser.isPresent());
        val persistedUser = optionalPersistedUser.get();
        assertNotNull(persistedUser.getId());
        assertEquals(nickname, persistedUser.getNickname());
        assertEquals(email, persistedUser.getEmail());
        assertEquals(biography, persistedUser.getBiography());
        assertEquals(password, persistedUser.getPassword());
        assertFalse(persistedUser.isActive());
    }

    @Test
    void Test003_GivenANonExistingNicknameWhenFindingByNicknameThenItIsNotPresent() {
        applicationUserRepository.save(applicationUser);

        val nonExistingUser = applicationUserRepository.findByNickname("Not a Nickname");

        assertTrue(nonExistingUser.isEmpty());
    }

    @Test
    void Test004_GivenAPersistedUserWhenFindingByEmailThenItIsReturned() {
        applicationUserRepository.save(applicationUser);


        val optionalPersistedUser = applicationUserRepository.findByEmail("some@email.com");

        assertTrue(optionalPersistedUser.isPresent());

        val persistedUser = optionalPersistedUser.get();

        assertNotNull(persistedUser.getId());
        assertEquals(nickname, persistedUser.getNickname());
        assertEquals(email, persistedUser.getEmail());
        assertEquals(biography, persistedUser.getBiography());
        assertEquals(password, persistedUser.getPassword());
        assertFalse(persistedUser.isActive());
    }

    @Test
    void Test005_GivenANonExistingEmailWhenFindingByEmailThenItIsNotPresent() {
        applicationUserRepository.save(applicationUser);

        val nonExistingUser = applicationUserRepository.findByEmail("nonPersisted@email.com");

        assertTrue(nonExistingUser.isEmpty());
    }
}
