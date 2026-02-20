package com.project.checkinn.user.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepo extends JpaRepository<Favorite, Long>, JpaSpecificationExecutor<Favorite> {
    boolean existsByUser_IdAndItemId(Long userId, Long itemId);

    Optional<Favorite> findByUser_IdAndItemId(Long userId, Long itemId);

    long deleteByUser_IdAndItemId(Long userId, Long itemId);

    List<Favorite> findByUser_Id(Long userId);
}
