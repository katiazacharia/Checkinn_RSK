package com.project.checkinn.notification;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements notificationService {

    private final NotificationRepo repo;

    public NotificationServiceImpl(NotificationRepo repo) {
        this.repo = repo;
    }

    @Override
    public NotificationResponse create(Notification notification) {
        if (notification.getTitle() == null || notification.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        Notification saved = repo.save(notification);
        return NotificationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll() {
        return repo.findAll().stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse findById(Long id) {
        Notification n = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        return NotificationMapper.toResponse(n);
    }

    @Override
    public NotificationResponse update(Long id, Notification newData) {
        Notification n = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        if (newData.getTitle() != null) n.setTitle(newData.getTitle());
        if (newData.getMessage() != null) n.setMessage(newData.getMessage());
        if (newData.getStatus() != null) n.setStatus(newData.getStatus());

        return NotificationMapper.toResponse(repo.save(n));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        repo.deleteById(id);
    }
}
