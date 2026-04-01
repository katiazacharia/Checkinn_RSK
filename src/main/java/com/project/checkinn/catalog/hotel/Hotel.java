package com.project.checkinn.catalog.hotel;

import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.room.Room;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private String name;

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Column(nullable = false)
    private String city;

    private String address;

    @Column(length = 1000)
    private String description;

    @OneToMany(
            mappedBy = "hotel",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Room> rooms = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hotel_amenities",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();

    public Hotel() {}

    // getters & setters

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<Room> getRooms() { return rooms; }
    public void setRooms(Set<Room> rooms) { this.rooms = rooms; }

    public Set<Amenity> getAmenities() { return amenities; }
    public void setAmenities(Set<Amenity> amenities) { this.amenities = amenities; }

    public void addAmenity(Amenity amenity) {
        if (amenity == null) return;
        this.amenities.add(amenity);
        amenity.getHotels().add(this);
    }

    public void removeAmenity(Amenity amenity) {
        if (amenity == null) return;
        this.amenities.remove(amenity);
        amenity.getHotels().remove(this);
    }

    public void clearAmenities() {
        for (Amenity a : new HashSet<>(this.amenities)) {
            removeAmenity(a);
        }
    }
}
