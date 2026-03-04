package com.project.checkinn.experienceplus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceExtraRepo extends JpaRepository<ExperienceExtra, Long> {
    List<ExperienceExtra> findByActiveTrue();

}
