package com.grimacegram.grimacegram.grimace;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Grimace {
    @Id
    @GeneratedValue
    private long id;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
}
