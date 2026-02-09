package com.project.checkinn.user.favorite;

import com.project.checkinn.user.profile.User;
import jakarta.persistence.*;

@Entity
@Table(name = "favorites")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
private User user;

    private Long itemId;

    public Favorite() {
    }

    public Favorite(User user, Long itemId) {
        this.user = user;
        this.itemId = itemId;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long placeId) {
        this.itemId = placeId;
    }
}


