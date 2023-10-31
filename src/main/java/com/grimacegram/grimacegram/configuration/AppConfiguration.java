package com.grimacegram.grimacegram.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "grimace")
@Data
public class AppConfiguration {

    String uploadPath;
    String profileImageFolder = "profile";
    String attachmentsFolder = "attachments";
    public String getFullProfileImagesPath() {
        return this.uploadPath + "/" + this.profileImageFolder;
    }

    public String getFullAttachmentsPath() {
        return this.uploadPath + "/" + this.attachmentsFolder;
    }
}
