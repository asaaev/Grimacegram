package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.configuration.AppConfiguration;
import com.grimacegram.grimacegram.repository.FileAttachmentRepository;
import com.grimacegram.grimacegram.services.FileService;
import com.grimacegram.grimacegram.shared.FileAttachment;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class FileServiceTest {
    FileService fileService;

    AppConfiguration appConfiguration;

    @MockBean
    FileAttachmentRepository fileAttachmentRepository;

    /**
     * Configuration and directory setup. Ensures that the file operations are performed in a controlled environment.
     * This includes creating necessary directories for storing test files.
     */
    @Before
    public void init(){
        appConfiguration = new AppConfiguration();
        appConfiguration.setUploadPath("upload-test");

        fileService = new FileService(appConfiguration, fileAttachmentRepository);

        new File(appConfiguration.getFullProfileImagesPath()).mkdir();
        new File(appConfiguration.getUploadPath()).mkdir();
        new File(appConfiguration.getFullAttachmentsPath()).mkdir();
    }
    @Test
    public void detectType_whenJpgFileProvided_returnsImageJpg() throws IOException {
        ClassPathResource resourceFile = new ClassPathResource("test-jpeg.jpeg");
        byte[] fileArr = FileUtils.readFileToByteArray(resourceFile.getFile());
        String fileType = fileService.detectType(fileArr);
        assertThat(fileType).isEqualToIgnoringCase("image/jpeg");
    }
    @Test
    public void cleanupStorage_whenOldFilesExist_removesFilesFromStorage() throws IOException {
        String fileName = "random-file";
        String filePath = appConfiguration.getFullAttachmentsPath() + "/" + fileName;
        File source = new ClassPathResource("profile.jpeg").getFile();
        File target = new File(filePath);
        FileUtils.copyFile(source, target);

    FileAttachment fileAttachment = new FileAttachment();
    fileAttachment.setId(5);
    fileAttachment.setName(fileName);

    Mockito.when(fileAttachmentRepository.findByDateBeforeAndGrimaceIsNull(Mockito.any(Date.class)))
            .thenReturn(Arrays.asList(fileAttachment));

    fileService.cleanupStorage();
    File storedImage = new File(filePath);
    assertThat(storedImage.exists()).isFalse();
}
    @Test
    public void cleanupStorage_whenOldFilesExist_removesFileAttachmentFromDatabase() throws IOException {
        String fileName = "random-file";
        String filePath = appConfiguration.getFullAttachmentsPath() + "/" + fileName;
        File source = new ClassPathResource("profile.jpeg").getFile();
        File target = new File(filePath);
        FileUtils.copyFile(source, target);

        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setId(5);
        fileAttachment.setName(fileName);

        Mockito.when(fileAttachmentRepository.findByDateBeforeAndGrimaceIsNull(Mockito.any(Date.class)))
                .thenReturn(Arrays.asList(fileAttachment));

        fileService.cleanupStorage();
        Mockito.verify(fileAttachmentRepository).deleteById(5L);
    }

    @After
    public void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(appConfiguration.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfiguration.getFullAttachmentsPath()));
    }

}
