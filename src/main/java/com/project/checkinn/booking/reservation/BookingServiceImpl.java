package com.project.checkinn.booking.reservation;

import com.project.checkinn.common.BookingStatus;
import com.project.checkinn.common.NotificationType;
import com.project.checkinn.notification.NotificationService;
import com.project.checkinn.promo.PromoCode;
import com.project.checkinn.user.profile.User;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.project.checkinn.catalog.room.Room;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final NotificationService notificationService;
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              EntityManager entityManager,
                              NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.entityManager = entityManager;
        this.notificationService = notificationService;
    }

    @Override
    public Booking create(BookingRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        if (request.getUserId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        if (request.getRoomId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roomId is required");

        if (request.getCheckInDate() == null || request.getCheckOutDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkInDate and checkOutDate are required");

        LocalDate in = request.getCheckInDate();
        LocalDate out = request.getCheckOutDate();

        if (!out.isAfter(in))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkOutDate must be after checkInDate");

        if (request.getTotalPrice() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "totalPrice is required");


        long conflicts = bookingRepository.countByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                request.getRoomId(),
                BookingStatus.CANCELLED,
                out,
                in);

        if (conflicts > 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dates not available for this room");

        User userRef = entityManager.find(User.class, request.getUserId());
        if (userRef == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        PromoCode promoRef = null;
        if (request.getPromoCodeId() != null) {
            promoRef = entityManager.getReference(PromoCode.class, request.getPromoCodeId());
        }

        Room roomRef = entityManager.find(Room.class, request.getRoomId());
        if (roomRef == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        Booking booking = BookingMapper.toEntity(request, userRef, roomRef, promoRef);
        booking.setStatus(BookingStatus.PENDING);



        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getByUser(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }

    @Override
    public Booking cancel(Long id) {
        Booking booking = getById(id);

        if (booking.getStatus() == BookingStatus.CANCELLED)
            return booking;

        LocalDate today = LocalDate.now();

        if (!booking.getCheckInDate().isAfter(today.plusDays(1))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cancellation not allowed (too late)"
            );
        }


        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        notificationService.create(
                saved.getUser().getId(),
                saved.getId(),
                NotificationType.EMAIL,
                "Booking Cancelled",
                "Your booking #" + saved.getId() + " has been cancelled."
        );

    return saved;
//        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> upcoming(Long userId) {
        LocalDate today = LocalDate.now();
        List<BookingStatus> statuses = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);
        if (userId == null) {
            return bookingRepository.findByCheckInDateGreaterThanEqualAndStatusIn(today, statuses);
        }
        return bookingRepository.findByUser_IdAndCheckInDateGreaterThanEqualAndStatusIn(userId, today, statuses);
    }

    @Override
    public List<Booking> search(BookingStatus status, Long userId, Long roomId, LocalDate from, LocalDate to) {
        return  bookingRepository.findAll().stream()
                .filter(b -> status == null || b.getStatus() == status)
                .filter(b -> userId == null || (b.getUser() != null && b.getUser().getId().equals(userId)))
                .filter(b -> roomId == null || (b.getRoom() != null && b.getRoom().getId().equals(roomId)))
                .filter(b -> from == null || !b.getCheckInDate().isBefore(from))
                .filter(b -> to == null || !b.getCheckOutDate().isAfter(to))
                .toList();
    }


}
