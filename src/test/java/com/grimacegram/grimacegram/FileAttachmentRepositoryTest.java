package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.repository.FileAttachmentRepository;
import com.grimacegram.grimacegram.shared.FileAttachment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class FileAttachmentRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    FileAttachmentRepository fileAttachmentRepository;

    @Test
    public void findByDateBeforeAndGrimaceIsNull_whenAttachmentsDateOlderThanOneHour_returnsAll(){
        testEntityManager.persist(getOneHourOldFileAttachment());
        testEntityManager.persist(getOneHourOldFileAttachment());
        testEntityManager.persist(getOneHourOldFileAttachment());
        Date oneHourAgo = new Date(System.currentTimeMillis() - (60*60*1000));
        List<FileAttachment> attachments = fileAttachmentRepository.findByDateBeforeAndGrimaceIsNull(oneHourAgo);
        assertThat(attachments.size()).isEqualTo(3);
    }

    @Test
    public void findByDateBeforeAndGrimaceIsNull_whenAttachmentsDateOlderThanOneHour_returnAll(){
        Grimace grimace1 = testEntityManager.persist(TestUtil.createValidGrimace());
        Grimace grimace2 = testEntityManager.persist(TestUtil.createValidGrimace());
        Grimace grimace3 = testEntityManager.persist(TestUtil.createValidGrimace());

        testEntityManager.persist(getOldFileAttachmentWithGrimace(grimace1));
        testEntityManager.persist(getOldFileAttachmentWithGrimace(grimace2));
        testEntityManager.persist(getOldFileAttachmentWithGrimace(grimace3));

        Date oneHourAgo = new Date(System.currentTimeMillis() - (60*60*1000));
        List<FileAttachment> attachments = fileAttachmentRepository.findByDateBeforeAndGrimaceIsNull(oneHourAgo);
        assertThat(attachments.size()).isEqualTo(0);
    }
    @Test
    public void findByDateBeforeAndGrimaceIsNull_whenAttachmentsDateWithinOneHour_returnsNone(){
        testEntityManager.persist(getFileAttachmentWithOneHour());
        testEntityManager.persist(getFileAttachmentWithOneHour());
        testEntityManager.persist(getFileAttachmentWithOneHour());
        Date oneHourAgo = new Date(System.currentTimeMillis() - (60*60*1000));
        List<FileAttachment> attachments = fileAttachmentRepository.findByDateBeforeAndGrimaceIsNull(oneHourAgo);
        assertThat(attachments.size()).isEqualTo(0);
    }
    @Test
    public void findByDateBeforeAndGrimaceIsNull_whenSomeAttachmentsOldSomeNewAndSomeWithGrimace_returnsNAttachmentsWithOlderAndNoGrimaceAssigned(){
        Grimace grimace1 = testEntityManager.persist(TestUtil.createValidGrimace());

        testEntityManager.persist(getOldFileAttachmentWithGrimace(grimace1));
        testEntityManager.persist(getOneHourOldFileAttachment());
        testEntityManager.persist(getFileAttachmentWithOneHour());
        Date oneHourAgo = new Date(System.currentTimeMillis() - (60*60*1000));
        List<FileAttachment> attachments = fileAttachmentRepository.findByDateBeforeAndGrimaceIsNull(oneHourAgo);
        assertThat(attachments.size()).isEqualTo(1);
    }

    private FileAttachment getOneHourOldFileAttachment(){
        Date date = new Date(System.currentTimeMillis() - (60*60*1000) - 1);
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setDate(date);
        return fileAttachment;
    }
    private FileAttachment getOldFileAttachmentWithGrimace(Grimace grimace){
        FileAttachment fileAttachment = getOneHourOldFileAttachment();
        fileAttachment.setGrimace(grimace);
        return fileAttachment;
    }
    private FileAttachment getFileAttachmentWithOneHour(){
        Date date = new Date(System.currentTimeMillis() - (60*1000));

        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setDate(date);
        return fileAttachment;
    }

}
