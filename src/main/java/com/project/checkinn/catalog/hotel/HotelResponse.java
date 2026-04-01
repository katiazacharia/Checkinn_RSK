package com.project.checkinn.catalog.hotel;

import com.project.checkinn.catalog.room.RoomResponse;

import java.util.List;
import java.util.Set;

public class HotelResponse {

    private Long id;
    private String name;
    private String city;
    private String address;
    private String description;
    private List<String> amenities;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;

    public HotelResponse() {}

    public HotelResponse(Long id, String name, String city, String address, String description,
                         List<String> amenities) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.description = description;
        this.amenities = amenities;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }
    public List<String> getAmenities() { return amenities; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCity(String city) { this.city = city; }
    public void setAddress(String address) { this.address = address; }
    public void setDescription(String description) { this.description = description; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
}