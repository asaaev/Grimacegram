package com.grimacegram.grimacegram.grimace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.shared.FileAttachment;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Entity
public class Grimace {
    @Id
    @GeneratedValue
    private long id;

    @NotNull
    @Size(min = 10, max = 5000)
    @Column(length = 5000)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @ManyToOne
    private User user;

    @OneToOne(mappedBy = "grimace", orphanRemoval = true)
    private FileAttachment attachment;
}
