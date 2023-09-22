package com.grimacegram.grimacegram.model;

import com.grimacegram.grimacegram.error.UniqueUsername;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private long userId;

    @NotNull(message = "{custom.grimacegram.userName.NotNull.message}")
    @Size(min = 4, max = 255, message = "{custom.grimacegram.userName.Size.message}")
    @UniqueUsername
    private String userName;
    @NotNull(message = "{custom.grimacegram.DisplayName.NotNull.message}")
    @Size(min = 4, max = 255)
    private String userDisplayName;
    @NotNull(message = "{custom.grimacegram.password.NotNul.message}")
    @Size(min = 8, max = 255)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$", message = "{custom.grimacegram.password.Pattern.message}")

    private String userPassword;
}
