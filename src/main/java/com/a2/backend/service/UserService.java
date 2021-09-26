package com.a2.backend.service;

import com.a2.backend.entity.User;
import com.a2.backend.model.*;

public interface UserService {

    User createUser(UserCreateDTO userCreateDTO);

    User confirmUser(ConfirmationTokenDTO confirmationTokenDTO);

    User recoverPassword(PasswordRecoveryDTO passwordRecoveryDTO);

    void deleteUser();

    User updateUser(UserUpdateDTO userUpdateDTO);

    void sendPasswordRecoveryMail(PasswordRecoveryInitDTO passwordRecoveryInitDTO);

    User updatePreferences(PreferencesUpdateDTO preferencesUpdateDTO);

    User getLoggedUser();
}
