package com.grimacegram.grimacegram.services;

import com.grimacegram.grimacegram.error.NotFoundException;
import com.grimacegram.grimacegram.file.FileService;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.UserRepository;
import com.grimacegram.grimacegram.vm.UserUpdateVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class UserService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    FileService fileService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
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

    public User getByUsername(String username) {
        User inDB = userRepository.findByUsername(username);
        if (inDB == null) {
            throw new NotFoundException(username + "not found");
        }
        return inDB;
    }

    public User update(long id, UserUpdateVM userUpdate) {
        User inDB = userRepository.getOne(id);
        inDB.setUserDisplayName(userUpdate.getDisplayName());
        if(userUpdate.getImage() != null){
            String saveImageName = null;
            try {
                saveImageName = fileService.saveProfileImage(userUpdate.getImage());
                inDB.setImage(saveImageName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return userRepository.save(inDB);
    }
}
