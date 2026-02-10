package com.project.checkinn.notification;


import com.project.checkinn.common.NotificationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse create(@RequestBody NotificationCreateRequest request) {

        if (request.getStatus() == null) {
            request.setStatus(NotificationStatus.UNREAD);
        }
        Notification created = notificationService.create(
                request.getUserId(),
                request.getBookingId(),
                request.getType(),
                request.getTitle(),
                request.getMessage()
        );

        created.setStatus(request.getStatus());
        return NotificationMapper.toResponse(created);
    }
    @GetMapping("/{id}")
    public NotificationResponse getById(@PathVariable Long id) {
        return NotificationMapper.toResponse(notificationService.getById(id));
    }
    @GetMapping
    public List<NotificationResponse> getAll() {
        return notificationService.getAll()
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }
}
