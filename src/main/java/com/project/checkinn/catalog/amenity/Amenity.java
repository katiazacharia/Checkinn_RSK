package com.project.checkinn.catalog.amenity;

import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.catalog.room.Room;
import com.project.checkinn.common.AmenityType;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String icon;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AmenityType type;



    @ManyToMany(mappedBy = "amenities")
    private Set<Hotel> hotels = new HashSet<>();

    @ManyToMany(mappedBy = "amenities")
    private Set<Room> rooms = new HashSet<>();

    public Amenity() {}


    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<Hotel> getHotels() { return hotels; }
    public void setHotels(Set<Hotel> hotels) { this.hotels = hotels; }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public AmenityType getType() {
        return type;
    }

    public void setType(AmenityType type) {
        this.type = type;
    }
}