package com.grimacegram.grimacegram.controllers;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.services.GrimaceService;
import com.grimacegram.grimacegram.shared.CurrentUser;
import com.grimacegram.grimacegram.vm.GrimaceVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/1.0")
public class GrimaceController {

    @Autowired
    GrimaceService grimaceService;

    @PostMapping("/grimace")
    GrimaceVM createGrimace(@Valid @RequestBody Grimace grimace, @CurrentUser User user){
        return new GrimaceVM(grimaceService.save(user, grimace));
    }
    @GetMapping("grimace")
    Page<GrimaceVM> getAllGrimaces(Pageable pageable){
        return grimaceService.getAllGrimaces(pageable).map(GrimaceVM::new);
    }
}
