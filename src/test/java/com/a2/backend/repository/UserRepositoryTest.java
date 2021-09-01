package com.a2.backend.repository;

import com.a2.backend.BackendApplication;
import com.a2.backend.entity.User;
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

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebClient
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({BackendApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    String nickname = "nickname";
    String email = "some@email.com";
    String biography = "bio";
    String password = "hashed_password";

    User user =
            User.builder()
                    .nickname(nickname)
                    .email(email)
                    .biography(biography)
                    .password(password)
                    .build();

    @Test
    void Test001_GivenAUserWhenSavingItThenItIsSaved() {

        assertTrue(userRepository.findAll().isEmpty());

        userRepository.save(user);

        val users = userRepository.findAll();

        assertEquals(1, users.size());

        User persistedUser = users.get(0);

        assertNotNull(persistedUser.getId());
        assertEquals(nickname, persistedUser.getNickname());
        assertEquals(email, persistedUser.getEmail());
        assertEquals(biography, persistedUser.getBiography());
        assertEquals(password, persistedUser.getPassword());
        assertFalse(persistedUser.isActive());
    }

    @Test
    void Test002_GivenAPersistedUserWhenFindingByNicknameThenItIsReturned() {
        userRepository.save(user);

        val optionalPersistedUser = userRepository.findByNickname("nickname");

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
        userRepository.save(user);

        val nonExistingUser = userRepository.findByNickname("Not a Nickname");

        assertTrue(nonExistingUser.isEmpty());
    }

    @Test
    void Test004_GivenAPersistedUserWhenFindingByEmailThenItIsReturned() {
        userRepository.save(user);

        val optionalPersistedUser = userRepository.findByEmail("some@email.com");

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
        userRepository.save(user);

        val nonExistingUser = userRepository.findByEmail("nonPersisted@email.com");

        assertTrue(nonExistingUser.isEmpty());
    }
}
