package com.grimacegram.grimacegram.controllers;

import com.grimacegram.grimacegram.services.FileService;
import com.grimacegram.grimacegram.shared.FileAttachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/1.0")
public class FileUploadController {

    @Autowired
    FileService fileService;

    @PostMapping("/grimace/upload")
    FileAttachment uploadForGrimace(MultipartFile file){
        return fileService.saveAttachment(file);
    }
}
