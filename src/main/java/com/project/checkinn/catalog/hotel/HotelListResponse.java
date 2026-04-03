package com.project.checkinn.catalog.hotel;

public class HotelListResponse {
    private Long id;
    private String name;

    public HotelListResponse() {
    }

    public HotelListResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
