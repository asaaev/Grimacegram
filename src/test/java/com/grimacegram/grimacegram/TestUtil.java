package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.model.User;

public class TestUtil {
    public static User createValidUser (){
        User user = new User();
        user.setUsername("test-user");
        user.setUserDisplayName("test-userDisplayName");
        user.setPassword("P4ssword");
        user.setImage("profile-image.png");
        return user;
    }
}
