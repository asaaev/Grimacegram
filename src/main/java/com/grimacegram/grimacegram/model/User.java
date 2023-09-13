package com.grimacegram.grimacegram.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private long userId;

    @NotNull
    private String userName;
    @NotNull
    private String userDisplayName;

    private String userPassword;
}
