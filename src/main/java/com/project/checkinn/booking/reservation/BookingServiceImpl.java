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


    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepo userRepository,
                              RoomRepo roomRepository,
                              PromoCodeRepository promoCodeRepository,
                              NotificationService notificationService,
                              PricingService pricingService,
                              ExperiencePlusService experiencePlusService, ExchangeRateService exchangeRateService, ExchangeRateConfig exchangeRateConfig ,LoyaltyService loyaltyService) {
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
    }

    @Override
    public Booking create(BookingRequest request, Authentication authentication) {
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

        Long userId = getUserId(authentication);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        long conflicts = bookingRepository.countByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                request.getRoomId(),
                BookingStatus.CANCELLED,
                out,
                in);

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

        CurrencyCode requestedCurrency =
                request.getCurrency() != null ? request.getCurrency() : exchangeRateConfig.getBaseCurrency();

        CurrencyCode baseCurrency = exchangeRateConfig.getBaseCurrency();

        if (roomRef.getStatus() != RoomStatus.AVAILABLE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is not available");

        if (request.getGuests() > roomRef.getCapacity())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guests exceed room capacity");

        BigDecimal basePrice =
                pricingService.calculateTotalPrice(roomRef, in, out);

        Booking booking = BookingMapper.toEntity(request, userRef, roomRef, promoRef);
        booking.setTotalPrice(basePrice);

        if (request.getPointsToRedeem() != null && request.getPointsToRedeem() > 0) {

            RedeemRequest redeemRequest = new RedeemRequest();
            redeemRequest.setUserId(userId);
            redeemRequest.setPoints(request.getPointsToRedeem());
            redeemRequest.setNote("Redeemed in booking");

            loyaltyService.redeem(redeemRequest);

            BigDecimal discount = BigDecimal.valueOf(request.getPointsToRedeem() * 0.05);

            BigDecimal maxDiscount = basePrice.multiply(BigDecimal.valueOf(0.2));

            if (discount.compareTo(maxDiscount) > 0) {
                discount = maxDiscount;
            }

            booking.setTotalPrice(basePrice.subtract(discount));
        }

        booking.setStatus(BookingStatus.PENDING);
        experiencePlusService.assignExtras(booking);


        int earnedPoints = booking.getTotalPrice().intValue();

        EarnRequest earnRequest = new EarnRequest();
        earnRequest.setUserId(userId);
        earnRequest.setPoints(earnedPoints);
        earnRequest.setNote("Earned from booking");

        loyaltyService.earn(earnRequest);


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
            return new BookingPreviewResponse(false, true, null,requestedCurrency.name(),null,null, "Room is not available");
        }

        if (request.getGuests() > roomRef.getCapacity()) {
            return new BookingPreviewResponse(false, false, null,requestedCurrency.name(),null,null, "Guests exceed room capacity");
        }

        long conflicts = bookingRepository.countByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                request.getRoomId(),
                BookingStatus.CANCELLED,
                out,
                in
        );

        if (conflicts > 0) {
            return new BookingPreviewResponse(false, true, null,requestedCurrency.name(),null,null, "Dates not available for this room");
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
        return new BookingPreviewResponse(true, true,finalPrice,requestedCurrency.name(),basePrice,exchangeRate, "Room is available");
    }


    private Long getUserId(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Jwt jwt)) return null;

        Object claim = jwt.getClaim("userId");
        if (claim == null) return null;

        if (claim instanceof Integer i) return i.longValue();
        if (claim instanceof Long l) return l;

        if (claim instanceof String s) {
            try {
                return Long.valueOf(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

}
