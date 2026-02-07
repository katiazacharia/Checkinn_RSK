package com.project.checkinn.booking.reservation;

import com.project.checkinn.common.BookingStatus;
import com.project.checkinn.promo.PromoCode;
import com.project.checkinn.user.profile.User;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;

    public BookingServiceImpl(BookingRepository bookingRepository, EntityManager entityManager) {
        this.bookingRepository = bookingRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Booking create(BookingRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        if (request.getUserId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        if (request.getCheckInDate() == null || request.getCheckOutDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkInDate and checkOutDate are required");

        LocalDate in = request.getCheckInDate();
        LocalDate out = request.getCheckOutDate();

        if (!out.isAfter(in))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkOutDate must be after checkInDate");

        if (request.getTotalPrice() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "totalPrice is required");

        // conflict check لسا مش جاهز حطيتو كومينت
      //  long conflicts = bookingRepository.countOverlappingBookings(in, out, BookingStatus.CANCELLED);
        // if (conflicts > 0) throw new ResponseStatusException(HttpStatus.CONFLICT, "Dates not available");

        User userRef = entityManager.getReference(User.class, request.getUserId());

        PromoCode promoRef = null;
        if (request.getPromoCodeId() != null) {
            promoRef = entityManager.getReference(PromoCode.class, request.getPromoCodeId());
        }

        Booking booking = BookingMapper.toEntity(request, userRef, promoRef);
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
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
}
