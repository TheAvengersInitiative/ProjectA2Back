package com.a2.backend.service;

import com.a2.backend.entity.User;
import com.a2.backend.model.PasswordRecoveryDTO;
import com.a2.backend.model.PasswordRecoveryInitDTO;
import com.a2.backend.model.PreferencesUpdateDTO;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.model.UserUpdateDTO;
import java.util.UUID;

public interface UserService {

    User createUser(UserCreateDTO userCreateDTO);

    User confirmUser(String token, UUID userid);

    User recoverPassword(PasswordRecoveryDTO passwordRecoveryDTO);

    void deleteUser();

    User updateUser(UserUpdateDTO userUpdateDTO);

    void sendPasswordRecoveryMail(PasswordRecoveryInitDTO passwordRecoveryInitDTO);

    User updatePreferences(PreferencesUpdateDTO preferencesUpdateDTO);

    User getLoggedUser();
}
