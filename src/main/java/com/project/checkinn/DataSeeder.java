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
import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
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
            appUser.setEnabled(true);
            appUser.setRole(role);
            appUser = appUserRepo.save(appUser);
        } else {
            appUser.setEnabled(true);
            appUser.setRole(role);
            appUser = appUserRepo.save(appUser);
        }

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

    private void seedAmenities(AmenityRepo repo) {

        //hotel amenities
        createAmenityIfMissing(repo, "WiFi", "wifi", "High-speed wireless internet available in all areas.");
        createAmenityIfMissing(repo, "Pool", "pool", "Outdoor swimming pool for guests.");
        createAmenityIfMissing(repo, "Parking", "car", "Free on-site parking.");
        createAmenityIfMissing(repo, "Gym", "dumbbell", "Fitness center with essential equipment.");
        createAmenityIfMissing(repo, "Breakfast", "utensils", "Daily breakfast available.");

        // Room amenities - new
        createAmenityIfMissing(repo, "Mini Bar", "wine", "In-room mini bar with drinks and snacks.");
        createAmenityIfMissing(repo, "Water Heater", "flame", "Electric water heater in room.");
        createAmenityIfMissing(repo, "Air Conditioning", "wind", "Climate control air conditioning.");
        createAmenityIfMissing(repo, "City View", "building", "Room with city view.");
        createAmenityIfMissing(repo, "Sea View", "waves", "Room with sea view.");
        createAmenityIfMissing(repo, "Sofa", "sofa", "Comfortable sofa in room.");
        createAmenityIfMissing(repo, "Balcony", "door-open", "Private balcony.");
        createAmenityIfMissing(repo, "Extra Bed for Baby", "baby", "Extra baby cot available on request.");
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
        h1.setName("CheckInn Grand Istanbul");
        h1.setCity("Istanbul");
        h1.setAddress("Sultanahmet, Istanbul");
        h1.setDescription("Luxury hotel in the heart of Istanbul with stunning Bosphorus views.");
        h1.setAmenities(new java.util.HashSet<>(java.util.List.of(
                wifi, parking, breakfast, gym
        ).stream().filter(java.util.Objects::nonNull).toList()));

        Hotel h2 = new Hotel();
        h2.setName("Ankara City Hotel");
        h2.setCity("Ankara");
        h2.setAddress("Kizilay, Ankara");
        h2.setDescription("Modern hotel in Turkey's capital with easy access to city attractions.");
        h2.setAmenities(new java.util.HashSet<>(java.util.List.of(
                wifi, pool, breakfast
        ).stream().filter(java.util.Objects::nonNull).toList()));

        Hotel h3 = new Hotel();
        h3.setName("Izmir Pearl");
        h3.setCity("Izmir");
        h3.setAddress("Kordon, Izmir");
        h3.setDescription("Cozy hotel by the Aegean sea with beautiful sunset views.");
        h3.setAmenities(new java.util.HashSet<>(java.util.List.of(
                wifi, parking
        ).stream().filter(java.util.Objects::nonNull).toList()));
        hotelRepo.save(h1);
        hotelRepo.save(h2);
        hotelRepo.save(h3);
    }

    private void seedRooms(HotelRepo hotelRepo, RoomRepo roomRepo, AmenityRepo amenityRepo) {
        if (roomRepo.count() > 0) return;

        List<Hotel> hotels = hotelRepo.findAll();
        if (hotels.size() < 3) return;

        Hotel istanbulHotel = hotels.get(0);
        Hotel ankaraHotel = hotels.get(1);
        Hotel izmirHotel = hotels.get(2);

        Amenity wifi = findAmenityByName(amenityRepo, "WiFi");
        Amenity miniBar = findAmenityByName(amenityRepo, "Mini Bar");
        Amenity waterHeater = findAmenityByName(amenityRepo, "Water Heater");
        Amenity ac = findAmenityByName(amenityRepo, "Air Conditioning");
        Amenity cityView = findAmenityByName(amenityRepo, "City View");
        Amenity seaView = findAmenityByName(amenityRepo, "Sea View");
        Amenity sofa = findAmenityByName(amenityRepo, "Sofa");
        Amenity balcony = findAmenityByName(amenityRepo, "Balcony");
        Amenity babyBed = findAmenityByName(amenityRepo, "Extra Bed for Baby");

        // Istanbul Hotel rooms
        createRoom(roomRepo, istanbulHotel, "101", RoomType.SINGLE, "50.00", 1, RoomStatus.AVAILABLE,
                new java.util.HashSet<>(java.util.List.of(wifi, ac, cityView).stream().filter(java.util.Objects::nonNull).toList()));

        createRoom(roomRepo, istanbulHotel, "102", RoomType.DOUBLE, "80.00", 2, RoomStatus.AVAILABLE,
                new java.util.HashSet<>(java.util.List.of(wifi, ac, miniBar, cityView, sofa).stream().filter(java.util.Objects::nonNull).toList()));

        createRoom(roomRepo, istanbulHotel, "201", RoomType.SUITE, "150.00", 4, RoomStatus.AVAILABLE,
                new java.util.HashSet<>(java.util.List.of(wifi, ac, miniBar, cityView, sofa, balcony, babyBed, waterHeater).stream().filter(java.util.Objects::nonNull).toList()));

        // Ankara Hotel rooms

        createRoom(roomRepo, ankaraHotel, "101", RoomType.SINGLE, "70.00", 1, RoomStatus.AVAILABLE,
                new java.util.HashSet<>(java.util.List.of(wifi, ac, seaView).stream().filter(java.util.Objects::nonNull).toList()));

        createRoom(roomRepo, ankaraHotel, "102", RoomType.DOUBLE, "110.00", 2, RoomStatus.AVAILABLE,
                new java.util.HashSet<>(java.util.List.of(wifi, ac, miniBar, seaView, balcony).stream().filter(java.util.Objects::nonNull).toList()));

        createRoom(roomRepo, ankaraHotel, "202", RoomType.SUITE, "220.00", 4, RoomStatus.AVAILABLE,
                new java.util.HashSet<>(java.util.List.of(wifi, ac, miniBar, seaView, sofa, balcony, babyBed, waterHeater).stream().filter(java.util.Objects::nonNull).toList()));

        // Izmir Hotel rooms
        createRoom(roomRepo, izmirHotel, "11", RoomType.SINGLE, "60.00", 1, RoomStatus.AVAILABLE,
                new java.util.HashSet<>(java.util.List.of(wifi, ac, cityView).stream().filter(java.util.Objects::nonNull).toList()));

        createRoom(roomRepo, izmirHotel, "12", RoomType.DOUBLE, "95.00", 2, RoomStatus.AVAILABLE,
                new java.util.HashSet<>(java.util.List.of(wifi, ac, miniBar, cityView, sofa).stream().filter(java.util.Objects::nonNull).toList()));

        createRoom(roomRepo, izmirHotel, "21", RoomType.SUITE, "180.00", 3, RoomStatus.AVAILABLE,
                new java.util.HashSet<>(java.util.List.of(wifi, ac, miniBar, cityView, sofa, balcony, waterHeater).stream().filter(java.util.Objects::nonNull).toList()));
    }
    private Amenity findAmenityByName(AmenityRepo repo, String name) {
        return repo.findAll()
                .stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }



    private void createRoom(RoomRepo repo, Hotel hotel, String roomNumber,
                            RoomType type, String price, int capacity, RoomStatus status,
                            java.util.Set<Amenity> amenities) {
        if (repo.existsByHotelIdAndRoomNumber(hotel.getId(), roomNumber)) return;

        Room room = new Room();
        room.setHotel(hotel);
        room.setRoomNumber(roomNumber);
        room.setType(type);
        room.setPricePerNight(new BigDecimal(price));
        room.setCapacity(capacity);
        room.setStatus(status);
        room.setAmenities(amenities);
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
}