package com.grimacegram.grimacegram.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * Represents a structured error response that the API returns when encountering exceptions or validation errors.
 * The class provides a clear structure for clients to understand the nature and specifics of the error.
 */
@Data
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ApiError {

    private long timestamp = new Date().getTime();
    private int status; // HTTP status code.
    private String message; // General error message.
    private String url; // URL where the error occurred.

    // Map holding specific validation error messages.
    // The key is the name of the field that had the validation error,
    // and the value is the error message associated with that field.

    private Map<String, String> validationErrors;

    //в UserController в методе handleValidationException, validationErrors заполняется на основе ошибок,
    // полученных из BindingResult. После этого, когда объект ApiError возвращается в ответ на запрос,
    // клиенты могут увидеть конкретные ошибки валидации и предпринять соответствующие действия
    // (например, показать пользователю сообщения об ошибках или исправить данные перед повторной попыткой).

    public ApiError(int status, String message, String url) {
        this.status = status;
        this.message = message;
        this.url = url;
    }
}