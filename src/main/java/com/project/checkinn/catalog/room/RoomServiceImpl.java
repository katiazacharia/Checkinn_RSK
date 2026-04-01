package com.project.checkinn.catalog.room;

import com.project.checkinn.ImageStorageService;
import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.catalog.hotel.HotelRepo;
import com.project.checkinn.common.CurrencyCode;
import com.project.checkinn.common.RoomStatus;
import com.project.checkinn.common.RoomType;
import com.project.checkinn.exchangerate.ExchangeRateConfig;
import com.project.checkinn.exchangerate.ExchangeRateService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;
    private final HotelRepo hotelRepo;
    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateConfig exchangeRateConfig;
    private final ImageStorageService imageStorageService;

    public RoomServiceImpl(RoomRepo roomRepo,
                           HotelRepo hotelRepo,
                           ExchangeRateService exchangeRateService,
                           ExchangeRateConfig exchangeRateConfig,
                           ImageStorageService imageStorageService) {
        this.roomRepo = roomRepo;
        this.hotelRepo = hotelRepo;
        this.exchangeRateService = exchangeRateService;
        this.exchangeRateConfig = exchangeRateConfig;
        this.imageStorageService = imageStorageService;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> getAll(
            Long hotelId,
            RoomType type,
            RoomStatus status,
            Integer minCapacity,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {

        Specification<Room> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hotelId != null) {
                predicates.add(cb.equal(root.get("hotel").get("id"), hotelId));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (minCapacity != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerNight"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePerNight"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return roomRepo.findAll(spec, pageable).map(RoomMapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public RoomResponse getById(Long id,CurrencyCode currency) {

        Room r = roomRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        RoomResponse response= RoomMapper.toResponse(r);

        CurrencyCode requestedCurrency = currency != null ? currency : exchangeRateConfig.getBaseCurrency();
        CurrencyCode baseCurrency = exchangeRateConfig.getBaseCurrency();


        if(!requestedCurrency.equals(baseCurrency)){
            BigDecimal rate = exchangeRateService.getRate(baseCurrency,requestedCurrency);
            BigDecimal convertedPrice = exchangeRateService.convert(
                r.getPricePerNight(),
                    baseCurrency,
                    requestedCurrency
            );

            response.setPricePerNight(convertedPrice);
            response.setCurrency(requestedCurrency.name());
            response.setOriginalPricePerNight(r.getPricePerNight());
            response.setExchangeRate(rate);

        }else{
            response.setPricePerNight(r.getPricePerNight());
            response.setCurrency(baseCurrency.name());
            response.setOriginalPricePerNight(r.getPricePerNight());
            response.setExchangeRate(BigDecimal.ONE);
        }
        return response;
    }

    @Override
    @Transactional
    public RoomResponse create(RoomRequest req) {

        if (req.getHotelId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hotelId is required");
        }
        if (req.getRoomNumber() == null || req.getRoomNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roomNumber is required");
        }
        if (req.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (req.getPricePerNight() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pricePerNight is required");
        }
        if (req.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }

        Hotel hotel = hotelRepo.findById(req.getHotelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        String roomNumber = req.getRoomNumber().trim();

        if (roomRepo.existsByHotelIdAndRoomNumber(req.getHotelId(), roomNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room number already exists in this hotel");
        }

        Room r = RoomMapper.toEntity(req, hotel);
        r.setRoomNumber(roomNumber);

        return RoomMapper.toResponse(roomRepo.save(r));
    }

    @Override
    @Transactional
    public RoomResponse update(Long id, RoomRequest req) {

        Room r = roomRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        if (req.getHotelId() != null && !req.getHotelId().equals(r.getHotel().getId())) {

            Hotel newHotel = hotelRepo.findById(req.getHotelId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

            r.setHotel(newHotel);
        }

        if (req.getRoomNumber() != null && !req.getRoomNumber().isBlank()) {

            String newNum = req.getRoomNumber().trim();
            Long hotelId = r.getHotel().getId();

            if (!newNum.equalsIgnoreCase(r.getRoomNumber())
                    && roomRepo.existsByHotelIdAndRoomNumber(hotelId, newNum)) {

                throw new ResponseStatusException(HttpStatus.CONFLICT, "Room number already exists in this hotel");
            }

            r.setRoomNumber(newNum);
        }

        if (req.getType() != null) r.setType(req.getType());
        if (req.getPricePerNight() != null) r.setPricePerNight(req.getPricePerNight());
        if (req.getCapacity() >= 0) r.setCapacity(req.getCapacity());
        if (req.getStatus() != null) r.setStatus(req.getStatus());

        return RoomMapper.toResponse(roomRepo.save(r));
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!roomRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }

        roomRepo.deleteById(id);
    }


    @Override
    public String uploadImage(Long roomId, MultipartFile file) {

        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        String imageUrl = imageStorageService.saveRoomImage(file);

        room.setImageUrl(imageUrl);
        roomRepo.save(room);

        return imageUrl;
    }

    }
