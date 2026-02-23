package com.project.checkinn.notification;

import com.project.checkinn.common.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    List<Notification> findByUser_IdOrderBySentAtDesc(Long userId);

    List<Notification> findByUser_IdAndStatusOrderBySentAtDesc(Long userId, NotificationStatus status);

    long countByUser_IdAndStatus(Long userId, NotificationStatus status);

}

