package com.grimacegram.grimacegram.repository;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrimaceRepository extends JpaRepository<Grimace, Long> {
    Page<Grimace> findByUser(User user, Pageable pageable);

    Page<Grimace> findByIdLessThan(long id, Pageable pageable);

    List<Grimace> findByIdGreaterThan(long id, Sort sort);

    Page<Grimace> findByIdLessThanAndUser(long id, User user, Pageable pageable);

    List<Grimace> findByIdGreaterThanAndUser(long id, User user, Sort sort);

    long countByIdGreaterThan(long id);
    long countByIdGreaterThanAndUser(long id, User user);
}
