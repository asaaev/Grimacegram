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
     * Fetches a list of Users based on the provided Pageable object and the current logged-in user.
     *
     * @param loggedInUser The User object of the currently logged-in user. If null, the function fetches all users.
     * @param pageable The Pageable object containing the pagination information.
     * @return A Page object containing the list of Users. The logged-in user is excluded if they are not null.
     */
    public Page<User> getUsers(User loggedInUser, Pageable pageable) {
        if(loggedInUser != null){
            return userRepository.findByUsernameNot(loggedInUser.getUsername(), pageable);
        }
        return userRepository.findAll(pageable);
    }
}
