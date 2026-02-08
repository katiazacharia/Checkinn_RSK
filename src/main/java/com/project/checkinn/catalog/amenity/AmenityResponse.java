package com.project.checkinn.catalog.amenity;

import java.util.Set;

public class AmenityResponse {
    private Long id;
    private String name;
    private String icon;
    private String description;

    // hotels that have this amenity (ids only)
    private Set<Long> hotelIds;

    public AmenityResponse() {}

    public AmenityResponse(Long id, String name, String icon, String description, Set<Long> hotelIds) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.hotelIds = hotelIds;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getIcon() { return icon; }
    public String getDescription() { return description; }
    public Set<Long> getHotelIds() { return hotelIds; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setDescription(String description) { this.description = description; }
    public void setHotelIds(Set<Long> hotelIds) { this.hotelIds = hotelIds; }
}