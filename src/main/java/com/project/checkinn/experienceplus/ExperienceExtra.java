package com.project.checkinn.experienceplus;

import com.project.checkinn.common.ExperienceExtraType;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
@Table(name = "experience_extras")
public class ExperienceExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceExtraType type;

    @Column(nullable = false)
    private BigDecimal minAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private int minNights = 0;

    @Column(nullable = false)
    private int minGuests = 0;

    @Column(nullable = false)
    private boolean active = true;

    public ExperienceExtra() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinNights() {
        return minNights;
    }

    public void setMinNights(int minNights) {
        this.minNights = minNights;
    }

    public int getMinGuests() {
        return minGuests;
    }

    public void setMinGuests(int minGuests) {
        this.minGuests = minGuests;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ExperienceExtraType getType() {
        return type;
    }

    public void setType(ExperienceExtraType type) {
        this.type = type;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }
}
