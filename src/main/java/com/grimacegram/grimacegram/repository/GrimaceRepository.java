package com.grimacegram.grimacegram.repository;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrimaceRepository extends JpaRepository<Grimace, Long> {
    Page<Grimace> findByUser(User user, Pageable pageable);
}
