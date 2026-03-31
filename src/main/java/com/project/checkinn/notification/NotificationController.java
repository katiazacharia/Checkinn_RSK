package com.project.checkinn.notification;


import com.project.checkinn.common.NotificationStatus;
import com.project.checkinn.common.NotificationType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
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

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/{id}")
    public NotificationResponse getById(@PathVariable Long id) {
        return NotificationMapper.toResponse(notificationService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping
    public List<NotificationResponse> getAll() {
        return notificationService.getAll()
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public List<NotificationResponse> myNotifications(Authentication authentication) {
        return notificationService.getMyNotifications(authentication)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }


        @GetMapping("/user/{userId}")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
        public List<NotificationResponse> getByUser(@PathVariable Long userId) {
            return notificationService.getByUser(userId)
                    .stream()
                    .map(NotificationMapper::toResponse)
                    .toList();
        }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/user/{userId}/unread")
    public List<NotificationResponse> getUnreadByUser(@PathVariable Long userId) {
            return notificationService.getByUserAndStatus(userId, NotificationStatus.UNREAD)
                    .stream()
                    .map(NotificationMapper::toResponse)
                    .toList();
        }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/unread")
    public List<NotificationResponse> myUnread(Authentication authentication) {
        return notificationService.getMyUnread(authentication)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/mark-read-all")
    public void markAllMyRead(Authentication authentication) {
        notificationService.markAllMyRead(authentication);
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

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/user/{userId}/mark-read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markReadAll(@PathVariable Long userId) {
        notificationService.markReadAll(userId);
    }


    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        notificationService.delete(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/search")
    public Page<NotificationResponse> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false, name = "q") String q,
            @PageableDefault(size = 10)
            @SortDefault(sort = "sentAt") Pageable pageable
    ) {
        return notificationService.search(userId, bookingId, type, status, from, to, q, pageable)
                .map(NotificationMapper::toResponse);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/search")
    public Page<NotificationResponse> searchMyNotifications(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false, name = "q") String q,
            @PageableDefault(size = 10)
            @SortDefault(sort = "sentAt") Pageable pageable,
            Authentication authentication
    ) {
        return notificationService
                .searchMyNotifications(bookingId, type, status, from, to, q,  authentication,pageable)
                .map(NotificationMapper::toResponse);
    }

}
