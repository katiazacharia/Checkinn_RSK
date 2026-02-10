package com.project.checkinn.user.favorite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepo extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser_Id(Long userId);
}
