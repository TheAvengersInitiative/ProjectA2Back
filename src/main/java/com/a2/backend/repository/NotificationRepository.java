package com.a2.backend.repository;

import com.a2.backend.entity.Notification;
import com.a2.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByUsersContaining(User user);
}
