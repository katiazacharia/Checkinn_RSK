package com.project.checkinn.catalog.amenity;

import com.project.checkinn.common.AmenityType;
import jakarta.validation.constraints.NotBlank;

public class AmenityRequest {
    private String name;
    private String icon;
    private String description;
    private AmenityType type;

    public AmenityRequest() {}

    @NotBlank
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AmenityType getType() {
        return type;
    }

    public void setType(AmenityType type) {
        this.type = type;
    }
}