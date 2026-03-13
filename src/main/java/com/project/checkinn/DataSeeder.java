package com.project.checkinn;

import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.amenity.AmenityRepo;
import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.catalog.hotel.HotelRepo;
import com.project.checkinn.catalog.room.Room;
import com.project.checkinn.catalog.room.RoomRepo;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            AppUserRepository appUserRepository,
            AmenityRepo amenityRepo,
            HotelRepo hotelRepo,
            RoomRepo roomRepo,
            PromoCodeRepository promoCodeRepository,
            ExperienceExtraRepo experienceExtraRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            seedUsers(appUserRepository, passwordEncoder);
            seedAmenities(amenityRepo);
            seedHotels(hotelRepo, amenityRepo);
            seedRooms(hotelRepo, roomRepo);
            seedPromoCodes(promoCodeRepository);
            seedExperienceExtras(experienceExtraRepo);
        };
    }

    private void seedUsers(AppUserRepository repo, PasswordEncoder encoder) {
        seedUser(repo, encoder, "admin", "Admin@123", Role.ADMIN);
        seedUser(repo, encoder, "manager", "Manager@123", Role.MANAGER);
        seedUser(repo, encoder, "customer1", "Cust@123", Role.CUSTOMER);
        seedUser(repo, encoder, "customer2", "Cust@123", Role.CUSTOMER);
    }

    private void seedUser(AppUserRepository repo, PasswordEncoder encoder,
                          String username, String rawPassword, Role role) {
        if (repo.existsByUsername(username)) {
            return;
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(rawPassword));
        user.setEnabled(true);
        user.setRole(role);
        repo.save(user);
    }

    private void seedAmenities(AmenityRepo repo) {
        createAmenityIfMissing(repo, "WiFi", "wifi", "High-speed wireless internet available in all areas.");
        createAmenityIfMissing(repo, "Pool", "pool", "Outdoor swimming pool for guests.");
        createAmenityIfMissing(repo, "Parking", "car", "Free on-site parking.");
        createAmenityIfMissing(repo, "Gym", "dumbbell", "Fitness center with essential equipment.");
        createAmenityIfMissing(repo, "Breakfast", "utensils", "Daily breakfast available.");
    }

    private void createAmenityIfMissing(AmenityRepo repo, String name, String icon, String description) {
        if (repo.existsByNameIgnoreCase(name)) {
            return;
        }

        Amenity amenity = new Amenity();
        amenity.setName(name);
        amenity.setIcon(icon);
        amenity.setDescription(description);
        repo.save(amenity);
    }

    private void seedHotels(HotelRepo hotelRepo, AmenityRepo amenityRepo) {
        if (hotelRepo.count() > 0) {
            return;
        }

        Amenity wifi = findAmenityByName(amenityRepo, "WiFi");
        Amenity pool = findAmenityByName(amenityRepo, "Pool");
        Amenity parking = findAmenityByName(amenityRepo, "Parking");
        Amenity gym = findAmenityByName(amenityRepo, "Gym");
        Amenity breakfast = findAmenityByName(amenityRepo, "Breakfast");

        Hotel h1 = new Hotel();
        h1.setName("CheckInn Grand Amman");
        h1.setCity("Amman");
        h1.setAddress("7th Circle, Amman");
        h1.setDescription("Modern hotel in Amman with comfortable rooms and premium services.");
        h1.setAmenities(new java.util.HashSet<>(java.util.List.of(
                wifi, parking, breakfast, gym
        ).stream().filter(java.util.Objects::nonNull).toList()));

        Hotel h2 = new Hotel();
        h2.setName("Aqaba Sea View");
        h2.setCity("Aqaba");
        h2.setAddress("Corniche Road, Aqaba");
        h2.setDescription("Sea-side stay with relaxing rooms and easy beach access.");
        h2.setAmenities(new java.util.HashSet<>(java.util.List.of(
                wifi, pool, breakfast
        ).stream().filter(java.util.Objects::nonNull).toList()));

        Hotel h3 = new Hotel();
        h3.setName("Petra Desert Lodge");
        h3.setCity("Petra");
        h3.setAddress("Tourism Street, Petra");
        h3.setDescription("Quiet lodge near Petra attractions with cozy rooms.");
        h3.setAmenities(new java.util.HashSet<>(java.util.List.of(
                wifi, parking
        ).stream().filter(java.util.Objects::nonNull).toList()));

        hotelRepo.save(h1);
        hotelRepo.save(h2);
        hotelRepo.save(h3);
    }

    private Amenity findAmenityByName(AmenityRepo repo, String name) {
        return repo.findAll()
                .stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private void seedRooms(HotelRepo hotelRepo, RoomRepo roomRepo) {
        if (roomRepo.count() > 0) {
            return;
        }

        List<Hotel> hotels = hotelRepo.findAll();
        if (hotels.size() < 3) {
            return;
        }

        Hotel ammanHotel = hotels.get(0);
        Hotel aqabaHotel = hotels.get(1);
        Hotel petraHotel = hotels.get(2);

        createRoom(roomRepo, ammanHotel, "101", RoomType.SINGLE, "50.00", 1, RoomStatus.AVAILABLE);
        createRoom(roomRepo, ammanHotel, "102", RoomType.DOUBLE, "80.00", 2, RoomStatus.AVAILABLE);
        createRoom(roomRepo, ammanHotel, "201", RoomType.SUITE, "150.00", 4, RoomStatus.AVAILABLE);

        createRoom(roomRepo, aqabaHotel, "101", RoomType.SINGLE, "70.00", 1, RoomStatus.AVAILABLE);
        createRoom(roomRepo, aqabaHotel, "102", RoomType.DOUBLE, "110.00", 2, RoomStatus.AVAILABLE);
        createRoom(roomRepo, aqabaHotel, "202", RoomType.SUITE, "220.00", 4, RoomStatus.AVAILABLE);

        createRoom(roomRepo, petraHotel, "11", RoomType.SINGLE, "60.00", 1, RoomStatus.AVAILABLE);
        createRoom(roomRepo, petraHotel, "12", RoomType.DOUBLE, "95.00", 2, RoomStatus.AVAILABLE);
        createRoom(roomRepo, petraHotel, "21", RoomType.SUITE, "180.00", 3, RoomStatus.AVAILABLE);
    }

    private void createRoom(RoomRepo repo, Hotel hotel, String roomNumber,
                            RoomType type, String price, int capacity, RoomStatus status) {
        if (repo.existsByHotelIdAndRoomNumber(hotel.getId(), roomNumber)) {
            return;
        }

        Room room = new Room();
        room.setHotel(hotel);
        room.setRoomNumber(roomNumber);
        room.setType(type);
        room.setPricePerNight(new BigDecimal(price));
        room.setCapacity(capacity);
        room.setStatus(status);
        repo.save(room);
    }

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

    private void seedExperienceExtras(ExperienceExtraRepo repo) {
        if (repo.count() > 0) {
            return;
        }

        ExperienceExtra e1 = new ExperienceExtra();
        e1.setName("Free Airport Transfer");
        e1.setType(ExperienceExtraType.FREE_AIRPORT_TRANSFER);
        e1.setMinAmount(new BigDecimal("50.00"));
        e1.setMinNights(1);
        e1.setMinGuests(1);
        e1.setActive(true);

        ExperienceExtra e2 = new ExperienceExtra();
        e2.setName("Room Upgrade");
        e2.setType(ExperienceExtraType.ROOM_UPGRADE);
        e2.setMinAmount(new BigDecimal("100.00"));
        e2.setMinNights(1);
        e2.setMinGuests(1);
        e2.setActive(true);

        ExperienceExtra e3 = new ExperienceExtra();
        e3.setName("Free Spa Access");
        e3.setType(ExperienceExtraType.FREE_SPA_ACCESS);
        e3.setMinAmount(new BigDecimal("120.00"));
        e3.setMinNights(2);
        e3.setMinGuests(1);
        e3.setActive(true);

        repo.save(e1);
        repo.save(e2);
        repo.save(e3);
    }
}