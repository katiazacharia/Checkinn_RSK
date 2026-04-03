package com.project.checkinn.catalog.amenity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.checkinn.common.AmenityType;

import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmenityResponse {
    private Long id;
    private String name;
    private String icon;
    private String description;
    private AmenityType type;

    public AmenityResponse() {

    }

    public AmenityResponse(Long id, String name, String icon, String description, AmenityType type){
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.type = type;
    }



    public Long getId() { return id; }
    public String getName() { return name; }
    public String getIcon() { return icon; }
    public String getDescription() { return description; }


    public AmenityType getType() {
        return type;
    }

    public void setType(AmenityType type) {
        this.type = type;
    }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setDescription(String description) { this.description = description; }
}