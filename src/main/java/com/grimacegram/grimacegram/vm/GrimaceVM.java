package com.grimacegram.grimacegram.vm;

import com.grimacegram.grimacegram.grimace.Grimace;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GrimaceVM {

    private long id;

    private String content;

    private long date;

    private UserVM user;

    private FileAttachmentVM attachment;

    public GrimaceVM(Grimace grimace) {
        this.setId(grimace.getId());
        this.setContent(grimace.getContent());
        this.setDate(grimace.getTimestamp().getTime());
        this.setUser(new UserVM(grimace.getUser()));
        if (grimace.getAttachment() != null) {
            this.setAttachment(new FileAttachmentVM(grimace.getAttachment()));
        }
    }
}
