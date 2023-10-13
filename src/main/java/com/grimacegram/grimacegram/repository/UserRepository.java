package com.grimacegram.grimacegram.repository;

import com.grimacegram.grimacegram.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Test method
     * @param username
     * @return
     */
    User findByUsername(String username);
}
