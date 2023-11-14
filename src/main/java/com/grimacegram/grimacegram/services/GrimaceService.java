package com.grimacegram.grimacegram.services;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.repository.GrimaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GrimaceService {

    GrimaceRepository grimaceRepository;
    public GrimaceService (GrimaceRepository grimaceRepository){
        super();
        this.grimaceRepository = grimaceRepository;
    }

    public void save(Grimace grimace) {
        grimace.setTimestamp(new Date());
        grimaceRepository.save(grimace);
    }

}
