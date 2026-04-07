package com.project.checkinn.booking.reservation;

import com.project.checkinn.catalog.room.Room;
import com.project.checkinn.common.BookingStatus;
import com.project.checkinn.common.CurrencyCode;
import com.project.checkinn.experienceplus.ExperienceExtra;
import com.project.checkinn.promo.PromoCode;
import com.project.checkinn.user.profile.User;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who booked
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // which room
    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private CurrencyCode currency;

    @Column(precision = 19, scale = 4)
    private BigDecimal originalTotalPrice;

    @Column(precision = 19, scale = 8)
    private BigDecimal exchangeRate;

    @Column(nullable = false)
    private int guests;

    // optional promo code
    @ManyToOne
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    @ManyToMany
    @JoinTable(
            name = "booking_extras",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "extra_id")
    )
    private Set<ExperienceExtra> extras = new HashSet<>();

    public Booking() {
    }

    // ===== getters & setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

   public Room getRoom() {
     return room;
    }

    public void setRoom(Room room) {
        this.room = room;
  }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getOriginalTotalPrice() {
        return originalTotalPrice;
    }

    public void setOriginalTotalPrice(BigDecimal originalTotalPrice) {
        this.originalTotalPrice = originalTotalPrice;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    public PromoCode getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(PromoCode promoCode) {
        this.promoCode = promoCode;
    }

    public int getGuests() { return guests; }

    public void setGuests(int guests) { this.guests = guests; }

    public Set<ExperienceExtra> getExtras() { return extras; }

    public void setExtras(Set<ExperienceExtra> extras) { this.extras = extras; }

}