package com.grimacegram.grimacegram.repository;

import com.grimacegram.grimacegram.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
}
