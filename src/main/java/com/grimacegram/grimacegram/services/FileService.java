package com.grimacegram.grimacegram.services;

import com.grimacegram.grimacegram.configuration.AppConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileService {
    AppConfiguration appConfiguration;

    public FileService(AppConfiguration appConfiguration){
        super();
        this.appConfiguration = appConfiguration;
    }

    public String saveProfileImage(String base64Image) throws IOException {
        String imageName = UUID.randomUUID().toString().replaceAll("-", "");
        byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
        File target = new File(appConfiguration.getFullProfileImagesPath() + "/" + imageName);
        FileUtils.writeByteArrayToFile(target, decodedBytes);
        return imageName;
    }

    public String detectType(byte[] fileArr) {
        Tika tika = new Tika();
        return tika.detect(fileArr);
    }

    public void deleteProfileImage(String image) {
        try {
            Files.deleteIfExists(Paths.get(appConfiguration.getFullProfileImagesPath()+"/"+image));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
