package com.project.checkinn.notification;

import com.project.checkinn.common.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_IdOrderBySentAtDesc(Long userId);

    List<Notification> findByUser_IdAndStatusOrderBySentAtDesc(Long userId, NotificationStatus status);

    long countByUser_IdAndStatus(Long userId, NotificationStatus status);

}
