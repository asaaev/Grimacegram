package com.grimacegram.grimacegram.controllers;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.services.GrimaceService;
import com.grimacegram.grimacegram.shared.CurrentUser;
import com.grimacegram.grimacegram.vm.GrimaceVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/users/{username}/grimace")
    Page<GrimaceVM> getGrimaceOfUser(@PathVariable String username, Pageable pageable){
        return grimaceService.getGrimaceOfUser(username, pageable).map(GrimaceVM::new);
    }

    @GetMapping({"/grimace/{id:[0-9]+}", "/users/{username}/grimace/{id:[0-9]+}"})
    ResponseEntity<?> getGrimaceRelative(@PathVariable long id,
                                         @PathVariable(required = false) String username,
                                         Pageable pageable,
                                         @RequestParam(name = "direction", defaultValue = "after") String direction,
                                         @RequestParam(name = "count", defaultValue = "false", required = false) boolean count){
        if (!direction.equalsIgnoreCase("after")) {
            return ResponseEntity.ok(grimaceService.getOldGrimaces(id, username, pageable).map(GrimaceVM::new));
        }
        if (count == true) {
            long newGrimaceCount = grimaceService.getNewGrimacesCount(id, username);
            return ResponseEntity.ok(Collections.singletonMap("count", newGrimaceCount));
        }
        List<GrimaceVM> newGrimace = grimaceService.getNewGrimace(id, username, pageable).stream().map(GrimaceVM::new).collect(Collectors.toList());
        return ResponseEntity.ok(newGrimace);
    }

}
