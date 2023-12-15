package com.grimacegram.grimacegram.services;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.GrimaceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GrimaceSecurityService {

    GrimaceRepository grimaceRepository;

    public GrimaceSecurityService(GrimaceRepository grimaceRepository) {
        this.grimaceRepository = grimaceRepository;
    }

    public boolean isAllowedToDelete(long grimaceId, User loggedUser){
        Optional<Grimace> optionalGrimace = grimaceRepository.findById(grimaceId);
        if (optionalGrimace.isPresent()){
            Grimace inDB = optionalGrimace.get();
            return (inDB.getUser().getUserId() == loggedUser.getUserId());
        }
        return false;
    }
}
