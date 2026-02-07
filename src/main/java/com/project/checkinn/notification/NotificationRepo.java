package com.project.checkinn.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
        Optional<Notification> findByTitleIgnoreCase(String title);
        boolean existsByTitleIgnoreCase(String title);
    }

