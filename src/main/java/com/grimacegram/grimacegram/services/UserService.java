package com.grimacegram.grimacegram.services;

import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Retrieves a paginated list of user projections.
     *
     * User projections (specified in UsersProjection interface) include selected user details and
     * allow for more efficient data retrieval and transmission. The method uses a predefined method
     * in UserRepository, which utilizes a native SQL query to fetch the data.
     *
     * Note: The method as it is will always retrieve the first page of users due to static
     * PageRequest. Consider parameterizing the page number and size for more dynamic pagination.
     *
     * @return a Page of user projections with specified details for each user.
     */
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
