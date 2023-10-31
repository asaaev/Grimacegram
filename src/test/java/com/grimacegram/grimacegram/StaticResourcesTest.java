package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.configuration.AppConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StaticResourcesTest {

@Autowired
    AppConfiguration appConfiguration;

    @Test
    public void checkStaticFolder_whenAppIsInitialized_uploadFolderMustExist(){
        File uploadFolder = new File(appConfiguration.getUploadPath());
        boolean uploadFolderExist = uploadFolder.exists() && uploadFolder.isDirectory();
        assertThat(uploadFolderExist).isTrue();
    }
    @Test
    public void checkStaticFolder_whenAppIsInitialized_profileImageSubFolderMustExist(){
        String profileImageFolderPath = appConfiguration.getFullProfileImagesPath();
        File profileImageFolder = new File(profileImageFolderPath);
        boolean profileImageFolderExist = profileImageFolder.exists() && profileImageFolder.isDirectory();
        assertThat(profileImageFolderExist).isTrue();
    }
    @Test
    public void checkStaticFolder_whenAppIsInitialized_attachmentsSubfolderMustExist(){
        String attachmentsFolderPath = appConfiguration.getFullAttachmentsPath();
        File attachmentFolder = new File(attachmentsFolderPath);
        boolean attachmentsFolderExist = attachmentFolder.exists() && attachmentFolder.isDirectory();
        assertThat(attachmentsFolderExist).isTrue();
    }

}
