package com.grimacegram.grimacegram.controllers;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.services.GrimaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/1.0")
public class GrimaceController {

    @Autowired
    GrimaceService grimaceService;

    @PostMapping("/grimace")
    void createGrimace(@Valid @RequestBody Grimace grimace){
        grimaceService.save(grimace);
    }
}
