package com.grimacegram.grimacegram.shared;

/**
 * A projection interface for fetching partial data of User entities.
 * This interface defines several getter methods, each of which corresponds to a field in the User entity.
 * During query execution, only the specified fields in the methods will be selected and fetched,
 * reducing the amount of data retrieved from the database and sent over the network.
 */
public interface UsersProjection {

    long getUserId();

    String getUsername();

    String getUserDisplayName();

    String getImage();
}
