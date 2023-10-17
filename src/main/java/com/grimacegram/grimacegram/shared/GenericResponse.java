package com.grimacegram.grimacegram.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Represents a general-purpose response object for the API.
 * This class is used to wrap simple messages in a structured format, ensuring consistent response patterns
 * across different API endpoints. The goal is to give feedback or convey information about the result of an operation.
 */
@Data
@NoArgsConstructor
public class GenericResponse {

    private String message; // A general message describing the result or the information.
    /**
     * Constructor that initializes the GenericResponse with a message.
     *
     * @param message the message to be set.
     */
    public GenericResponse(String message) {
        this.message = message;
    }
}
