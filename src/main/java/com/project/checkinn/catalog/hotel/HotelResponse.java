package com.project.checkinn.catalog.hotel;

import java.util.Set;

public class HotelResponse {
    private Long id;
    private String name;
    private String city;
    private String address;
    private String description;

    private Set<Long> roomIds;
    private Set<Long> amenityIds;

    public HotelResponse() {}

    public HotelResponse(Long id, String name, String city, String address, String description,
                         Set<Long> roomIds, Set<Long> amenityIds) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.description = description;
        this.roomIds = roomIds;
        this.amenityIds = amenityIds;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }
    public Set<Long> getRoomIds() { return roomIds; }
    public Set<Long> getAmenityIds() { return amenityIds; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCity(String city) { this.city = city; }
    public void setAddress(String address) { this.address = address; }
    public void setDescription(String description) { this.description = description; }
    public void setRoomIds(Set<Long> roomIds) { this.roomIds = roomIds; }
    public void setAmenityIds(Set<Long> amenityIds) { this.amenityIds = amenityIds; }
}