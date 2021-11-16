package com.a2.backend.repository;

import com.a2.backend.entity.Notification;
import com.a2.backend.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findAllByUserToNotify(User userToNotify);
}
