package com.grimacegram.grimacegram.repository;

import com.grimacegram.grimacegram.shared.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {

    List<FileAttachment> findByDateBeforeAndGrimaceIsNull(Date date);
}
