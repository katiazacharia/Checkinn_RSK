package com.project.checkinn;

import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.amenity.AmenityRepo;
import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.catalog.hotel.HotelRepo;
import com.project.checkinn.catalog.room.Room;
import com.project.checkinn.catalog.room.RoomRepo;
import com.project.checkinn.common.AmenityType;
import com.project.checkinn.common.ExperienceExtraType;
import com.project.checkinn.common.RoomStatus;
import com.project.checkinn.common.RoomType;
import com.project.checkinn.experienceplus.ExperienceExtra;
import com.project.checkinn.experienceplus.ExperienceExtraRepo;
import com.project.checkinn.promo.PromoCode;
import com.project.checkinn.promo.PromoCodeRepository;
import com.project.checkinn.security.AppUser;
import com.project.checkinn.security.AppUserRepository;
import com.project.checkinn.security.Role;
import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            AppUserRepository appUserRepository,
            UserRepo userRepo,
            AmenityRepo amenityRepo,
            HotelRepo hotelRepo,
            RoomRepo roomRepo,
            PromoCodeRepository promoCodeRepository,
            ExperienceExtraRepo experienceExtraRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            seedUsers(appUserRepository, userRepo, passwordEncoder);
            seedAmenities(amenityRepo);
            seedHotels(hotelRepo, amenityRepo);
            seedRooms(hotelRepo, roomRepo, amenityRepo);
            seedPromoCodes(promoCodeRepository);
            seedExperienceExtras(experienceExtraRepo);
        };
    }

    // ========================= USERS =========================

    private void seedUsers(AppUserRepository appUserRepo, UserRepo userRepo, PasswordEncoder encoder) {
        seedUser(appUserRepo, userRepo, encoder,
                "admin", "Admin@123", Role.ADMIN,
                "System Admin", "admin@test.com", "0590000001");

        seedUser(appUserRepo, userRepo, encoder,
                "manager", "Manager@123", Role.MANAGER,
                "Hotel Manager", "manager@test.com", "0590000002");

        seedUser(appUserRepo, userRepo, encoder,
                "customer1", "Cust@123", Role.CUSTOMER,
                "Customer One", "customer1@test.com", "0590000003");

        seedUser(appUserRepo, userRepo, encoder,
                "customer2", "Cust@123", Role.CUSTOMER,
                "Customer Two", "customer2@test.com", "0590000004");
    }

    private void seedUser(
            AppUserRepository appUserRepo,
            UserRepo userRepo,
            PasswordEncoder encoder,
            String username,
            String rawPassword,
            Role role,
            String fullName,
            String email,
            String phone
    ) {
        AppUser appUser = appUserRepo.findByUsername(username).orElse(null);

        if (appUser == null) {
            appUser = new AppUser();
            appUser.setUsername(username);
            appUser.setPasswordHash(encoder.encode(rawPassword));
        }

        appUser.setEnabled(true);
        appUser.setRole(role);
        appUser = appUserRepo.save(appUser);

        User profile = userRepo.findByAppUserId(appUser.getId()).orElse(null);
        if (profile == null) {
            profile = new User();
            profile.setAppUser(appUser);
        }

        profile.setFullName(fullName);
        profile.setEmail(email);
        profile.setPhone(phone);
        profile.setRole(role);

        userRepo.save(profile);
    }

    // ========================= AMENITIES =========================

    private void seedAmenities(AmenityRepo repo) {
        // Hotel amenities
        upsertAmenity(repo, "WiFi", "wifi", "High-speed wireless internet available in all areas.", AmenityType.HOTEL);
        upsertAmenity(repo, "Pool", "pool", "Outdoor swimming pool for guests.", AmenityType.HOTEL);
        upsertAmenity(repo, "Parking", "car", "Free on-site parking.", AmenityType.HOTEL);
        upsertAmenity(repo, "Gym", "dumbbell", "Fitness center with essential equipment.", AmenityType.HOTEL);
        upsertAmenity(repo, "Breakfast", "utensils", "Daily breakfast available.", AmenityType.HOTEL);

        // Room amenities
        upsertAmenity(repo, "Mini Bar", "wine", "In-room mini bar with drinks and snacks.", AmenityType.ROOM);
        upsertAmenity(repo, "Water Heater", "flame", "Electric water heater in room.", AmenityType.ROOM);
        upsertAmenity(repo, "Air Conditioning", "wind", "Climate control air conditioning.", AmenityType.ROOM);
        upsertAmenity(repo, "City View", "building", "Room with city view.", AmenityType.ROOM);
        upsertAmenity(repo, "Sea View", "waves", "Room with sea view.", AmenityType.ROOM);
        upsertAmenity(repo, "Sofa", "sofa", "Comfortable sofa in room.", AmenityType.ROOM);
        upsertAmenity(repo, "Balcony", "door-open", "Private balcony.", AmenityType.ROOM);
        upsertAmenity(repo, "Extra Bed for Baby", "baby", "Extra baby cot available on request.", AmenityType.ROOM);
    }

    private void upsertAmenity(AmenityRepo repo, String name, String icon, String description, AmenityType type) {
        Amenity amenity = repo.findAll()
                .stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(new Amenity());

        amenity.setName(name);
        amenity.setIcon(icon);
        amenity.setDescription(description);
        amenity.setType(type);

        repo.save(amenity);
    }

    // ========================= HOTELS =========================

    private void seedHotels(HotelRepo hotelRepo, AmenityRepo amenityRepo) {
        Hotel h1 = findHotelByName(hotelRepo, "CheckInn Grand Istanbul");
        if (h1 == null) h1 = new Hotel();
        h1.setName("CheckInn Grand Istanbul");
        h1.setCity("Istanbul");
        h1.setAddress("Sultanahmet, Istanbul");
        h1.setDescription("Luxury hotel in the heart of Istanbul with stunning Bosphorus views.");
        h1.setAmenities(hotelAmenitySet(
                findAmenityByName(amenityRepo, "WiFi"),
                findAmenityByName(amenityRepo, "Parking"),
                findAmenityByName(amenityRepo, "Breakfast"),
                findAmenityByName(amenityRepo, "Gym")
        ));
        h1 = hotelRepo.save(h1);

        Hotel h2 = findHotelByName(hotelRepo, "Ankara City Hotel");
        if (h2 == null) h2 = new Hotel();
        h2.setName("Ankara City Hotel");
        h2.setCity("Ankara");
        h2.setAddress("Kizilay, Ankara");
        h2.setDescription("Modern hotel in Turkey's capital with easy access to city attractions.");
        h2.setAmenities(hotelAmenitySet(
                findAmenityByName(amenityRepo, "WiFi"),
                findAmenityByName(amenityRepo, "Pool"),
                findAmenityByName(amenityRepo, "Breakfast")
        ));
        h2 = hotelRepo.save(h2);

        Hotel h3 = findHotelByName(hotelRepo, "Izmir Pearl");
        if (h3 == null) h3 = new Hotel();
        h3.setName("Izmir Pearl");
        h3.setCity("Izmir");
        h3.setAddress("Kordon, Izmir");
        h3.setDescription("Cozy hotel by the Aegean sea with beautiful sunset views.");
        h3.setAmenities(hotelAmenitySet(
                findAmenityByName(amenityRepo, "WiFi"),
                findAmenityByName(amenityRepo, "Parking")
        ));
        hotelRepo.save(h3);
    }

    // ========================= ROOMS =========================

    private void seedRooms(HotelRepo hotelRepo, RoomRepo roomRepo, AmenityRepo amenityRepo) {
        Hotel istanbulHotel = requireHotelByName(hotelRepo, "CheckInn Grand Istanbul");
        Hotel ankaraHotel = requireHotelByName(hotelRepo, "Ankara City Hotel");
        Hotel izmirHotel = requireHotelByName(hotelRepo, "Izmir Pearl");

        // Istanbul Hotel rooms
        upsertRoom(roomRepo, istanbulHotel, "101", RoomType.SINGLE, "50.00", 1, RoomStatus.AVAILABLE,
                roomAmenitySet(
                        findAmenityByName(amenityRepo, "Air Conditioning"),
                        findAmenityByName(amenityRepo, "City View")
                ));

        upsertRoom(roomRepo, istanbulHotel, "102", RoomType.DOUBLE, "80.00", 2, RoomStatus.AVAILABLE,
                roomAmenitySet(
                        findAmenityByName(amenityRepo, "Air Conditioning"),
                        findAmenityByName(amenityRepo, "Mini Bar"),
                        findAmenityByName(amenityRepo, "City View"),
                        findAmenityByName(amenityRepo, "Sofa")
                ));

        upsertRoom(roomRepo, istanbulHotel, "201", RoomType.SUITE, "150.00", 4, RoomStatus.AVAILABLE,
                roomAmenitySet(
                        findAmenityByName(amenityRepo, "Air Conditioning"),
                        findAmenityByName(amenityRepo, "Mini Bar"),
                        findAmenityByName(amenityRepo, "City View"),
                        findAmenityByName(amenityRepo, "Sofa"),
                        findAmenityByName(amenityRepo, "Balcony"),
                        findAmenityByName(amenityRepo, "Extra Bed for Baby"),
                        findAmenityByName(amenityRepo, "Water Heater")
                ));

        // Ankara Hotel rooms
        upsertRoom(roomRepo, ankaraHotel, "101", RoomType.SINGLE, "70.00", 1, RoomStatus.AVAILABLE,
                roomAmenitySet(
                        findAmenityByName(amenityRepo, "Air Conditioning"),
                        findAmenityByName(amenityRepo, "Sea View")
                ));

        upsertRoom(roomRepo, ankaraHotel, "102", RoomType.DOUBLE, "110.00", 2, RoomStatus.AVAILABLE,
                roomAmenitySet(
                        findAmenityByName(amenityRepo, "Air Conditioning"),
                        findAmenityByName(amenityRepo, "Mini Bar"),
                        findAmenityByName(amenityRepo, "Sea View"),
                        findAmenityByName(amenityRepo, "Balcony")
                ));

        upsertRoom(roomRepo, ankaraHotel, "202", RoomType.SUITE, "220.00", 4, RoomStatus.AVAILABLE,
                roomAmenitySet(
                        findAmenityByName(amenityRepo, "Air Conditioning"),
                        findAmenityByName(amenityRepo, "Mini Bar"),
                        findAmenityByName(amenityRepo, "Sea View"),
                        findAmenityByName(amenityRepo, "Sofa"),
                        findAmenityByName(amenityRepo, "Balcony"),
                        findAmenityByName(amenityRepo, "Extra Bed for Baby"),
                        findAmenityByName(amenityRepo, "Water Heater")
                ));

        // Izmir Hotel rooms
        upsertRoom(roomRepo, izmirHotel, "11", RoomType.SINGLE, "60.00", 1, RoomStatus.AVAILABLE,
                roomAmenitySet(
                        findAmenityByName(amenityRepo, "Air Conditioning"),
                        findAmenityByName(amenityRepo, "City View")
                ));

        upsertRoom(roomRepo, izmirHotel, "12", RoomType.DOUBLE, "95.00", 2, RoomStatus.AVAILABLE,
                roomAmenitySet(
                        findAmenityByName(amenityRepo, "Air Conditioning"),
                        findAmenityByName(amenityRepo, "Mini Bar"),
                        findAmenityByName(amenityRepo, "City View"),
                        findAmenityByName(amenityRepo, "Sofa")
                ));

        upsertRoom(roomRepo, izmirHotel, "21", RoomType.SUITE, "180.00", 3, RoomStatus.AVAILABLE,
                roomAmenitySet(
                        findAmenityByName(amenityRepo, "Air Conditioning"),
                        findAmenityByName(amenityRepo, "Mini Bar"),
                        findAmenityByName(amenityRepo, "City View"),
                        findAmenityByName(amenityRepo, "Sofa"),
                        findAmenityByName(amenityRepo, "Balcony"),
                        findAmenityByName(amenityRepo, "Water Heater")
                ));
    }

    private void upsertRoom(RoomRepo repo, Hotel hotel, String roomNumber,
                            RoomType type, String price, int capacity, RoomStatus status,
                            Set<Amenity> amenities) {
        Room room = repo.findAll()
                .stream()
                .filter(r -> r.getHotel() != null
                        && r.getHotel().getId().equals(hotel.getId())
                        && r.getRoomNumber().equalsIgnoreCase(roomNumber))
                .findFirst()
                .orElse(new Room());

        room.setHotel(hotel);
        room.setRoomNumber(roomNumber);
        room.setType(type);
        room.setPricePerNight(new BigDecimal(price));
        room.setCapacity(capacity);
        room.setStatus(status);
        room.setAmenities(amenities);

        repo.save(room);
    }

    // ========================= PROMO CODES =========================

    private void seedPromoCodes(PromoCodeRepository repo) {
        createPromoIfMissing(repo, "WELCOME10", new BigDecimal("10.00"),
                LocalDate.now().minusDays(5), LocalDate.now().plusMonths(2), true);

        createPromoIfMissing(repo, "SUMMER15", new BigDecimal("15.00"),
                LocalDate.now().minusDays(2), LocalDate.now().plusMonths(1), true);

        createPromoIfMissing(repo, "VIP25", new BigDecimal("25.00"),
                LocalDate.now().minusDays(1), LocalDate.now().plusMonths(3), true);
    }

    private void createPromoIfMissing(PromoCodeRepository repo, String code,
                                      BigDecimal discountValue,
                                      LocalDate validFrom,
                                      LocalDate validTo,
                                      boolean active) {
        if (repo.existsByCodeIgnoreCase(code)) {
            return;
        }

        PromoCode promo = new PromoCode();
        promo.setCode(code);
        promo.setDiscountValue(discountValue);
        promo.setValidFrom(validFrom);
        promo.setValidTo(validTo);
        promo.setActive(active);

        repo.save(promo);
    }

    // ========================= EXPERIENCE EXTRAS =========================

    private void seedExperienceExtras(ExperienceExtraRepo repo) {
        if (repo.count() > 0) {
            return;
        }

        ExperienceExtra e1 = new ExperienceExtra();
        e1.setName("Free Airport Transfer");
        e1.setType(ExperienceExtraType.FREE_AIRPORT_TRANSFER);
        e1.setMinAmount(new BigDecimal("250.00"));
        e1.setMinNights(1);
        e1.setMinGuests(1);
        e1.setActive(true);

        ExperienceExtra e2 = new ExperienceExtra();
        e2.setName("Room Upgrade");
        e2.setType(ExperienceExtraType.ROOM_UPGRADE);
        e2.setMinAmount(new BigDecimal("400.00"));
        e2.setMinNights(1);
        e2.setMinGuests(1);
        e2.setActive(true);

        ExperienceExtra e3 = new ExperienceExtra();
        e3.setName("Free Spa Access");
        e3.setType(ExperienceExtraType.FREE_SPA_ACCESS);
        e3.setMinAmount(new BigDecimal("50.00"));
        e3.setMinNights(2);
        e3.setMinGuests(1);
        e3.setActive(true);

        repo.save(e1);
        repo.save(e2);
        repo.save(e3);
    }

    // ========================= HELPERS =========================

    private Amenity findAmenityByName(AmenityRepo repo, String name) {
        return repo.findAll()
                .stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private Hotel findHotelByName(HotelRepo repo, String name) {
        return repo.findAll()
                .stream()
                .filter(h -> h.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private Hotel requireHotelByName(HotelRepo repo, String name) {
        Hotel hotel = findHotelByName(repo, name);
        if (hotel == null) {
            throw new IllegalStateException("Hotel not found while seeding: " + name);
        }
        return hotel;
    }

    private Set<Amenity> hotelAmenitySet(Amenity... amenities) {
        Set<Amenity> result = new HashSet<>();
        for (Amenity amenity : amenities) {
            if (amenity != null && amenity.getType() == AmenityType.HOTEL) {
                result.add(amenity);
            }
        }
        return result;
    }

    private Set<Amenity> roomAmenitySet(Amenity... amenities) {
        Set<Amenity> result = new HashSet<>();
        for (Amenity amenity : amenities) {
            if (amenity != null && amenity.getType() == AmenityType.ROOM) {
                result.add(amenity);
            }
        }
        return result;
    }
}