package com.project.checkinn.notification;


import com.project.checkinn.common.NotificationStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse create(@RequestBody NotificationRequest request) {

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
        @GetMapping("/user/{userId}")
    public List<NotificationResponse> getByUser(@PathVariable Long userId) {
            return notificationService.getByUser(userId)
                    .stream()
                    .map(NotificationMapper::toResponse)
                    .toList();
        }
        @GetMapping("/User/{userId}/unread")
    public List<NotificationResponse> getUnreadByUser(@PathVariable Long userId) {
            return notificationService.getByUserAndStatus(userId, NotificationStatus.UNREAD)
                    .stream()
                    .map(NotificationMapper::toResponse)
                    .toList();
        }

        //ال default من الservice هو UNREAD
    @PutMapping("/{id}/status")
    public NotificationResponse updateStatus(
            @PathVariable Long id,
            @RequestParam("value") NotificationStatus status
    ) {
        return NotificationMapper.toResponse(notificationService.updateStatus(id, status));
    }

    //يعني حتى لو ما قرات النوتوفيكيشن بكون مبعوت ومخلص
    @PutMapping("/{id}/mark-read")
    public NotificationResponse markRead(@PathVariable Long id) {
        return NotificationMapper.toResponse(notificationService.markRead(id));
    }
    @PutMapping("/user/{userId}/mark-read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markReadAll(@PathVariable Long userId) {
        notificationService.markReadAll(userId);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        notificationService.delete(id);
    }

}
