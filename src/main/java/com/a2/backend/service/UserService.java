package com.a2.backend.service;

import com.a2.backend.entity.User;
import com.a2.backend.model.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User createUser(UserCreateDTO userCreateDTO);

    User confirmUser(ConfirmationTokenDTO confirmationTokenDTO);

    User recoverPassword(PasswordRecoveryDTO passwordRecoveryDTO);

    void deleteUser();

    User updateUser(UserUpdateDTO userUpdateDTO);

    void sendPasswordRecoveryMail(PasswordRecoveryInitDTO passwordRecoveryInitDTO);

    User updatePreferences(PreferencesUpdateDTO preferencesUpdateDTO);

    User getLoggedUser();

    List<ProjectDTO> getPreferredProjects();

    Optional<User> getUser();

    User updatePrivacySettings(UserPrivacyDTO userPrivacyDTO);

    UserProfileDTO getUserProfile(UUID id);

    User getUser(UUID id);

    User updateReputation(UUID id);

    List<ReviewDTO> getUserReviews(UUID id);

    boolean switchEmailNotificationPreferences(
            NotificationUpdatePreferencDTO notificationUpdatePreferencDTO);
}
