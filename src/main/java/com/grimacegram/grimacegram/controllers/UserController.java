package com.grimacegram.grimacegram.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.grimacegram.grimacegram.error.ApiError;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.UserRepository;
import com.grimacegram.grimacegram.services.UserService;
import com.grimacegram.grimacegram.shared.GenericResponse;
import com.grimacegram.grimacegram.shared.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0")
public class UserController {
    @Autowired
    UserService userService;


    @PostMapping("/users")
    GenericResponse createUser(@Valid @RequestBody User user){
        userService.save(user);
        return new GenericResponse("User");
    }
    @GetMapping("/users")
    @JsonView(Views.Base.class)
    Page<?> getUsers(){
        return userService.getUsers();
    }

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
    /*
     * Указывает, что метод должен быть вызван,
     * когда исключение MethodArgumentNotValidException возникает в контроллере. Такое исключение обычно генерируется,
     * когда аргументы, переданные методу контроллера, не прошли проверку валидности.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    /*
     * Устанавливает HTTP-статус ответа, который будет возвращен клиенту, если этот обработчик исключений вызывается.
     * В данном случае, статус будет 400 Bad Request, что подразумевает, что клиент отправил запрос,
     * который сервер не может или не хочет обрабатывать из-за неверных данных.
     */
    ApiError handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request){
        /*
            Внутри метода, мы сначала создаем объект ApiError с некоторым базовым описанием ошибки.
            Затем мы проходим через все ошибки валидации, вытягивая из BindingResult, и добавляем их в наш объект ошибки.
            Это обеспечивает структурированный ответ об ошибке, который можно легко интерпретировать на стороне клиента.
        */
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
