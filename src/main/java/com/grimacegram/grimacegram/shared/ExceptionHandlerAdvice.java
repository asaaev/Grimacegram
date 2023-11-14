package com.grimacegram.grimacegram.shared;

import com.grimacegram.grimacegram.error.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    /**
     * Handles validation exceptions thrown when request payload validation rules are violated.
     *
     * @param exception the MethodArgumentNotValidException exception thrown due to validation failure.
     * @param request the HttpServletRequest object associated with the request leading to the exception.
     * @return ApiError object populated with validation errors and associated details.
     *
     * This method gets triggered when the payload of a request fails validation. For example, if a required field is missing
     * or some value doesn't meet the expected constraints. The method constructs an ApiError object to provide a structured
     * error response, detailing the specific validation errors that occurred.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
//     Indicates that the method should be invoked
//     when a MethodArgumentNotValidException arises within the controller. Such an exception is typically generated
//     when arguments passed to a controller method fail validation checks.
    @ResponseStatus(HttpStatus.BAD_REQUEST)

//     Sets the HTTP status of the response to be returned to the client if this exception handler gets invoked.
//     In this case, the status will be 400 Bad Request, implying that the client sent a request
//     which the server cannot or will not process due to erroneous data.


    ApiError handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request){

//            Inside the method, we initially craft an ApiError object with a general error description.
//            We then iterate through all the validation errors, extracted from BindingResult, and append them to our error object.
//            This ensures a structured error response that can be easily interpreted on the client's end.

        ApiError apiError = new ApiError(400, "Validation error", request.getServletPath());
        BindingResult result = exception.getBindingResult();
        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError fieldError: result.getFieldErrors()
        ) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        apiError.setValidationErrors(validationErrors);
        return apiError;
    }
}
