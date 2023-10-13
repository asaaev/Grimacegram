package com.grimacegram.grimacegram.configuration;

import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 * Service class responsible for authenticating users based on the information retrieved from the UserRepository.
 * This implementation adheres to Spring Security's UserDetailsService interface, ensuring seamless integration
 * with Spring's built-in security functionalities.
 */
@Service
public class AuthUserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    /**
     * Load user-specific data based on the provided username.
     *
     * This method is an override from the UserDetailsService interface and it's typically used behind the scenes by Spring Security.
     * The logic is straightforward: It fetches the user data from the repository.
     * If the user doesn't exist, it throws an exception. Otherwise, it returns the user details.
     *
     * @param username the name of the user to be fetched.
     * @return UserDetails object which Spring Security will use for authentication and authorization.
     *
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
