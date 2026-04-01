package com.project.checkinn.catalog.amenity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmenityResponse {
    private Long id;
    private String name;
    private String icon;
    private String description;

    // hotels that have this amenity (ids only)
    private Set<String> hotelNames;

    public AmenityResponse() {}

    public AmenityResponse(Long id, String name, String icon, String description, Set<String> hotelNames) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.hotelNames = hotelNames;
    }



    public Long getId() { return id; }
    public String getName() { return name; }
    public String getIcon() { return icon; }
    public String getDescription() { return description; }
    public Set<String> getHotelNames() { return hotelNames; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setDescription(String description) { this.description = description; }
    public void setHotelNames(Set<String> hotelNames) { this.hotelNames = hotelNames; }
}