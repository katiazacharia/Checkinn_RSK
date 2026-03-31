package com.project.checkinn.loyalty.account;

import com.project.checkinn.common.Tier;
import com.project.checkinn.user.profile.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private int points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tier tier = Tier.BRONZE;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public LoyaltyAccount() {}

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }


//    public enum Tier { BRONZE, SILVER, GOLD }

    public void recalculateTier() {
        if (this.points >= 1500)     this.tier = Tier.GOLD;
        else if (this.points >= 500) this.tier = Tier.SILVER;
        else                         this.tier = Tier.BRONZE;
    }

    public Tier getTier() { return tier; }
}

