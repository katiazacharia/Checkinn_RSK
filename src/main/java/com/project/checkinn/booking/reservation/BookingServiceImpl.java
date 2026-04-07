package com.project.checkinn.booking.reservation;

import com.project.checkinn.booking.preview.BookingPreviewRequest;
import com.project.checkinn.booking.preview.BookingPreviewResponse;
import com.project.checkinn.booking.pricing.PricingService;
import com.project.checkinn.catalog.room.RoomRepo;
import com.project.checkinn.common.BookingStatus;
import com.project.checkinn.common.CurrencyCode;
import com.project.checkinn.common.NotificationType;
import com.project.checkinn.common.RoomStatus;
import com.project.checkinn.exchangerate.ExchangeRateConfig;
import com.project.checkinn.exchangerate.ExchangeRateService;
import com.project.checkinn.experienceplus.ExperiencePlusService;
import com.project.checkinn.loyalty.EarnRequest;
import com.project.checkinn.loyalty.LoyaltyService;
import com.project.checkinn.loyalty.RedeemRequest;
import com.project.checkinn.notification.NotificationService;
import com.project.checkinn.promo.PromoCode;
import com.project.checkinn.promo.PromoCodeRepository;
import com.project.checkinn.security.CurrentUserService;
import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import com.project.checkinn.catalog.room.Room;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final NotificationService notificationService;
    private final BookingRepository bookingRepository;
    private final PricingService pricingService;
    private final UserRepo userRepository;
    private final RoomRepo roomRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final ExperiencePlusService experiencePlusService;
    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateConfig exchangeRateConfig;
    private final LoyaltyService loyaltyService;
    private final CurrentUserService currentUserService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepo userRepository,
                              RoomRepo roomRepository,
                              PromoCodeRepository promoCodeRepository,
                              NotificationService notificationService,
                              PricingService pricingService,
                              ExperiencePlusService experiencePlusService, ExchangeRateService exchangeRateService, ExchangeRateConfig exchangeRateConfig , LoyaltyService loyaltyService, CurrentUserService currentUserService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.promoCodeRepository = promoCodeRepository;
        this.notificationService = notificationService;
        this.pricingService = pricingService;
        this.experiencePlusService = experiencePlusService;
        this.exchangeRateService = exchangeRateService;
        this.exchangeRateConfig = exchangeRateConfig;
        this.loyaltyService = loyaltyService;
        this.currentUserService = currentUserService;
    }

    @Override
    public Booking createMyBooking(BookingRequest request, Authentication authentication) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        if (request.getRoomId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roomId is required");

        if (request.getCheckInDate() == null || request.getCheckOutDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkInDate and checkOutDate are required");

        LocalDate in = request.getCheckInDate();
        LocalDate out = request.getCheckOutDate();

        if (!out.isAfter(in))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkOutDate must be after checkInDate");

        Long userId = currentUserService.getCurrentUserId(authentication);

        long conflicts = bookingRepository.countByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                request.getRoomId(),
                BookingStatus.CANCELLED,
                out,
                in
        );

        if (conflicts > 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dates not available for this room");

        User userRef = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        PromoCode promoRef = null;
        if (request.getPromoCodeId() != null) {
            promoRef = promoCodeRepository.findById(request.getPromoCodeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promo code not found"));
        }

        Room roomRef = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        if (roomRef.getStatus() != RoomStatus.AVAILABLE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is not available");

        if (request.getGuests() > roomRef.getCapacity())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guests exceed room capacity");


        BigDecimal basePrice = pricingService.calculateTotalPrice(roomRef, in, out);



        CurrencyCode baseCurrency = exchangeRateConfig.getBaseCurrency();
        CurrencyCode requestedCurrency = request.getCurrency() != null ? request.getCurrency() : baseCurrency;

        BigDecimal exchangeRate;
        BigDecimal finalPrice;

        if (requestedCurrency == baseCurrency) {
            exchangeRate = BigDecimal.ONE;
            finalPrice = basePrice.setScale(2, RoundingMode.HALF_UP);
        } else {
            exchangeRate = exchangeRateService.getRate(baseCurrency, requestedCurrency);
            finalPrice = exchangeRateService.convert(basePrice, baseCurrency, requestedCurrency);
        }

        Booking booking = BookingMapper.toEntity(request, userRef, roomRef, promoRef);
        booking.setOriginalTotalPrice(basePrice);
        booking.setCurrency(requestedCurrency);
        booking.setExchangeRate(exchangeRate);
        booking.setTotalPrice(finalPrice);
        booking.setStatus(BookingStatus.PENDING);
        experiencePlusService.assignExtras(booking);

        Booking savedBooking = bookingRepository.save(booking);


        return savedBooking;
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

    @Override
    public BookingPreviewResponse preview(BookingPreviewRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        if (request.getRoomId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roomId is required");

        if (request.getCheckInDate() == null || request.getCheckOutDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkInDate and checkOutDate are required");
        if (request.getGuests() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "guests must be greater than 0");

        LocalDate in = request.getCheckInDate();
        LocalDate out = request.getCheckOutDate();

        if (!out.isAfter(in))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkOutDate must be after checkInDate");


        Room roomRef = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        CurrencyCode requestedCurrency =
                request.getCurrency() != null ? request.getCurrency() : exchangeRateConfig.getBaseCurrency();

        CurrencyCode baseCurrency = exchangeRateConfig.getBaseCurrency();

        if (roomRef.getStatus() != RoomStatus.AVAILABLE) {
            return new BookingPreviewResponse(false, true, null,requestedCurrency,null,null, "Room is not available");
        }

        if (request.getGuests() > roomRef.getCapacity()) {
            return new BookingPreviewResponse(false, false, null,requestedCurrency,null,null, "Guests exceed room capacity");
        }

        long conflicts = bookingRepository.countByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                request.getRoomId(),
                BookingStatus.CANCELLED,
                out,
                in
        );

        if (conflicts > 0) {
            return new BookingPreviewResponse(false, true, null,requestedCurrency,null,null, "Dates not available for this room");
        }

        BigDecimal basePrice = pricingService.calculateTotalPrice(roomRef, in, out);

        BigDecimal finalPrice;
        BigDecimal exchangeRate;

        if (requestedCurrency == baseCurrency) {
            finalPrice = basePrice;
            exchangeRate = BigDecimal.ONE;
        } else {
            exchangeRate = exchangeRateService.getRate(baseCurrency, requestedCurrency);
            finalPrice = exchangeRateService.convert(basePrice, baseCurrency, requestedCurrency);
        }
        return new BookingPreviewResponse(true, true,finalPrice,requestedCurrency,basePrice,exchangeRate, "Room is available");
    }


    @Override
    public List<Booking> getMyBookings(Authentication authentication) {
        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        return bookingRepository.findByUser_Id(currentUserId);
    }

}
