package com.project.checkinn.catalog.hotel;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;



public interface HotelService {

    String uploadImage(Long hotelId, MultipartFile file);


    Page<HotelResponse> getAll(Pageable pageable);

    HotelResponse getById(Long id);

    HotelResponse create(HotelRequest req);

    HotelResponse update(Long id, HotelRequest req);

    void delete(Long id);

    Page<HotelResponse> search(String city, String name, Long amenityId, Pageable pageable);

    HotelResponse addAmenity(Long hotelId, Long amenityId);

    HotelResponse removeAmenity(Long hotelId, Long amenityId);

    HotelResponse replaceAmenities(Long hotelId, Set<Long> amenityIds);

}
