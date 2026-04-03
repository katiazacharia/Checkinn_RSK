package com.project.checkinn.catalog.hotel;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;

public class HotelDetailsResponse {

    private Long id;
    private String name;
    private String city;
    private String address;
    private String description;
    private List<String> imageUrls = new ArrayList<>();

    public HotelDetailsResponse() {}

    public HotelDetailsResponse(Long id, String name, String city, String address, String description , List<String> imageUrls) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.description = description;
        this.imageUrls = imageUrls;

    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCity(String city) { this.city = city; }
    public void setAddress(String address) { this.address = address; }
    public void setDescription(String description) { this.description = description; }
}