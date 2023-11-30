package com.grimacegram.grimacegram.services;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.GrimaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GrimaceService {

    GrimaceRepository grimaceRepository;
    UserService userService;
    public GrimaceService (GrimaceRepository grimaceRepository, UserService userService){
        super();
        this.grimaceRepository = grimaceRepository;
        this.userService = userService;
    }

    public Grimace save(User user, Grimace grimace) {
        grimace.setTimestamp(new Date());
        grimace.setUser(user);
        return grimaceRepository.save(grimace);
    }

    public Page<Grimace> getAllGrimaces(Pageable pageable) {
        return grimaceRepository.findAll(pageable);
    }

    public Page<Grimace> getGrimaceOfUser(String username, Pageable pageable) {
        User inDB = userService.getByUsername(username);
        return grimaceRepository.findByUser(inDB, pageable);
    }
}
