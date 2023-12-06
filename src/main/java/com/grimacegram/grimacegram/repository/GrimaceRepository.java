package com.grimacegram.grimacegram.repository;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GrimaceRepository extends JpaRepository<Grimace, Long>, JpaSpecificationExecutor<Grimace> {
    Page<Grimace> findByUser(User user, Pageable pageable);

}
