package com.grimacegram.grimacegram.vm;

import com.grimacegram.grimacegram.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserVM (User ViewModel) provides a more straightforward and explicit way
 * to represent user data for serialization and transportation.
 *
 * This approach offers clarity and simplicity in representing user data
 * without the need for additional serialization configurations or projections.
 */
@Data
@NoArgsConstructor
public class UserVM {

    private long userId;

    private String username;

    private String userDisplayName;

    private String image;

    public UserVM(User user) {
        this.setUserId(user.getUserId());
        this.setUsername(user.getUsername());
        this.setUserDisplayName(user.getUserDisplayName());
        this.setImage(user.getImage());
    }
}
