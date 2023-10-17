package com.grimacegram.grimacegram.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
/**
 * Centralized error handling controller.
 * This class acts as the primary handler for exceptions and errors that aren't handled at the method level in other controllers.
 * It ensures that a structured error response is returned for unexpected errors, rather than exposing internal details.
 */
@RestController
public class ErrorHandler implements ErrorController {
    /**
     * Handles the generation of structured error responses.
     *
     * @param webRequest the current web request associated with the error.
     * @return ApiError object populated with error details.
     *
     * This method captures the attributes of the error (like status, message, and path)
     * and wraps them in the ApiError object to standardize the error response format.
     */
    @Autowired
    private ErrorAttributes errorAttributes;
    //Если в вашем приложении происходит исключение или ошибка, которую не обрабатывает другой обработчик исключений,
    // то управление переходит к этому контроллеру ошибок.

    //Метод handleError занимается извлечением деталей ошибки из текущего WebRequest
    // (например, статуса ошибки, сообщения и URL) и возвращает эти детали в стандартизированном формате
    // с помощью объекта ApiError.

    //Метод getErrorPath просто указывает путь, по которому будет активирован контроллер ошибок.
    // В большинстве случаев это просто "/error", но это можно изменить
    @RequestMapping("/error")
    ApiError handleError(WebRequest webRequest){
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions
                .of(ErrorAttributeOptions.Include.MESSAGE));
        String message = (String) attributes.get("message");
        String url = (String) attributes.get("path");
        int status = (Integer) attributes.get("status");
        return new ApiError(status, message, url);
    }
    /**
     * The path where the error controller will trigger.
     *
     * @return the error path (in this case, "/error").
     */
    public String getErrorPath(){
        return "/error";
    }
}
