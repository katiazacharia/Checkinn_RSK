package com.project.checkinn.catalog.hotel;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;



public interface HotelService {

    String uploadImage(Long hotelId, MultipartFile file);

    Page<HotelListResponse> getAll(Pageable pageable);

    HotelDetailsResponse getById(Long id);

    HotelDetailsResponse getByName(String name);

    HotelDetailsResponse create(HotelRequest req, Authentication authentication);

    HotelDetailsResponse update(Long id, HotelRequest req);

    void delete(Long id);

    Page<HotelDetailsResponse> search(String city, String name, Long amenityId, Pageable pageable);

    HotelDetailsResponse addAmenity(Long hotelId, Long amenityId);

    HotelDetailsResponse removeAmenity(Long hotelId, Long amenityId);

    HotelDetailsResponse replaceAmenities(Long hotelId, Set<Long> amenityIds);

}
