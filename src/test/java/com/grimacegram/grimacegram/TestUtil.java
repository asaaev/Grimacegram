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
    public static User createValidUser (String username){
        User user = createValidUser();
        user.setUsername(username);
        return user;
    }
}
