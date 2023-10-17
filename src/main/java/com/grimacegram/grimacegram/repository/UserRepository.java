package com.grimacegram.grimacegram.repository;

import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.shared.UsersProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    /**
     * Retrieves a paginated view of all users with selected details (projection).
     *
     * @param pageable the pagination and sorting information.
     * @return a Page of UsersProjection objects containing selected user details for the requested page.
     */
    @Query(value = "SELECT * FROM users", nativeQuery = true)
    Page<UsersProjection> getAllUsersProjection(Pageable pageable);
}
