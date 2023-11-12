package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.configuration.AppConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StaticResourcesTest {

@Autowired
    AppConfiguration appConfiguration;
@Autowired
    MockMvc mockMvc;

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
    @Test
    public void getStaticFile_whenImageExistInProfileUploadFolder_receiveOk() throws Exception {
        String fileName = "profile-picture.jpg";
        File source = new ClassPathResource("profile.jpg").getFile();

        File target = new File(appConfiguration.getFullProfileImagesPath() + "/" + fileName);
        FileUtils.copyFile(source, target);

        mockMvc.perform(get("/images/"+appConfiguration.getProfileImageFolder()+"/"+fileName)).andExpect(status().isOk());
    }
    @Test
    public void getStaticFile_whenImageExistInAttachmentFolder_receiveOk() throws Exception {
        String fileName = "profile-picture.jpg";
        File source = new ClassPathResource("profile.jpg").getFile();

        File target = new File(appConfiguration.getFullAttachmentsPath() + "/" + fileName);
        FileUtils.copyFile(source, target);

        mockMvc.perform(get("/images/"+appConfiguration.getAttachmentsFolder()+"/"+fileName)).andExpect(status().isOk());
    }
    @Test
    public void getStaticFile_whenImageDoesNotExist_receiveNotFound() throws Exception {
        mockMvc.perform(get("/images/"+appConfiguration.getAttachmentsFolder()+"/there-is-no-such-image.jpg"))
                .andExpect(status().isNotFound());
    }
    @After
    public void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(appConfiguration.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfiguration.getFullAttachmentsPath()));
    }

}
