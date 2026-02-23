package com.project.checkinn.notification;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.booking.reservation.BookingRepository;
import com.project.checkinn.common.NotificationStatus;
import com.project.checkinn.common.NotificationType;
import com.project.checkinn.user.profile.User;

import com.project.checkinn.user.profile.UserRepo;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final UserRepo userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Page<Notification> search(
            Long userId,
            Long bookingId,
            NotificationType type,
            NotificationStatus status,
            LocalDateTime from,
            LocalDateTime to,
            String q,
            Pageable pageable
    ) {
        Specification<Notification> spec = Specification.where(NotificationSpecifications.userId(userId))
                .and(NotificationSpecifications.bookingId(bookingId))
                .and(NotificationSpecifications.type(type))
                .and(NotificationSpecifications.status(status))
                .and(NotificationSpecifications.sentFrom(from))
                .and(NotificationSpecifications.sentTo(to))
                .and(NotificationSpecifications.q(q));

        return notificationRepository.findAll(spec, pageable);
    }


    private final NotificationRepository notificationRepository;
    private final EntityManager entityManager;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   EntityManager entityManager,
                                   UserRepo userRepository,
                                   BookingRepository bookingRepository) {
        this.notificationRepository = notificationRepository;
        this.entityManager = entityManager;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Notification create(
            Long userId,
            Long bookingId,
            NotificationType type,
            String title,
            String message
    ) {

        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        if (type == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");

        if (title == null || title.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");

        if (message == null || message.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message is required");


        Notification notification = new Notification();
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "User with id " + userId + " not found"));

        notification.setUser(user);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Booking with id " + bookingId + " not found"));

        notification.setBooking(booking);

        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setSentAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    @Override
    public Notification createFromRequest(NotificationRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        Notification n = create(
                request.getUserId(),
                request.getBookingId(),
                request.getType(),
                request.getTitle(),
                request.getMessage()
        );

        if (request.getStatus() != null) {
            n.setStatus(request.getStatus());
            return notificationRepository.save(n);
        }

        return n;
    }

    @Override
    public Notification getById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
    }

    @Override
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getByUser(Long userId) {
        return notificationRepository.findByUser_IdOrderBySentAtDesc(userId);
    }

    @Override
    public List<Notification> getByUserAndStatus(Long userId, NotificationStatus status) {
        return notificationRepository.findByUser_IdAndStatusOrderBySentAtDesc(userId, status);

    }

    @Override
    public Notification updateStatus(Long id, NotificationStatus status) {
        Notification n = getById(id);
        n.setStatus(status);
        return notificationRepository.save(n);
    }

    @Override
    public Notification markRead(Long id) {
        Notification n = getById(id);
        if (n.getStatus() == NotificationStatus.UNREAD) {
            n.setStatus(NotificationStatus.SENT);
            return notificationRepository.save(n);
        }
        return n;
    }

    @Override
    public void markReadAll(Long userId) {
        List<Notification> unread = getByUserAndStatus(userId, NotificationStatus.UNREAD);
        for (Notification n : unread) {
            n.setStatus(NotificationStatus.SENT);
        }
        notificationRepository.saveAll(unread);
    }

    @Override
    public void delete(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        notificationRepository.deleteById(id);
    }


}
