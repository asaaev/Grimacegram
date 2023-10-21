package com.grimacegram.grimacegram.model;

import com.grimacegram.grimacegram.shared.UniqueUsername;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.beans.Transient;
import java.util.Collection;

/**
 * Represents a user within the application.
 * This entity class not only holds the user details but also ensures that it adheres to Spring Security's UserDetails interface.
 * This allows the application to leverage Spring Security's built-in mechanisms for user authentication and authorization.
 */
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private long userId;
    // Custom validation messages can be generated for the constraints

    @NotNull(message = "{custom.grimacegram.userName.NotNull.message}")
    @Size(min = 4, max = 255, message = "{custom.grimacegram.userName.Size.message}")
    @UniqueUsername
    private String username;

    @NotNull(message = "{custom.grimacegram.DisplayName.NotNull.message}")
    @Size(min = 4, max = 255)
    private String userDisplayName;

    @NotNull(message = "{custom.grimacegram.password.NotNul.message}")
    @Size(min = 8, max = 255)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$", message = "{custom.grimacegram.password.Pattern.message}")

    private String password;

    private String image;

    /**
     * Returns the authorities granted to the user.
     * In this case, every user is granted the "Role_USER" authority.
     *
     * @return a collection of granted authorities.
     */
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("Role_USER");
    }
    /**
     * Indicates whether the user's account has expired or not.
     *
     * @return true since it's always active in this implementation.
     */
    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }
    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true since it's always unlocked in this implementation.
     */
    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }
    /**
     * Indicates whether the user's credentials (password) are expired or not.
     *
     * @return true since it's always non-expired in this implementation.
     */

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true since it's always enabled in this implementation.
     */
    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }
}
