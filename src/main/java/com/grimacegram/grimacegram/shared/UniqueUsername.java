package com.grimacegram.grimacegram.shared;

import com.grimacegram.grimacegram.error.UniqueUsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Custom validation annotation for ensuring a unique username.
 * Can be applied to fields or method return types.
 *
 * This annotation leverages the UniqueUsernameValidator class to provide validation logic.
 * It's intended to be used on the 'username' field (or similar) to guarantee its uniqueness across the application.
 */
@Constraint(validatedBy = UniqueUsernameValidator.class)
//указывает, что UniqueUsername является пользовательской аннотацией валидации, и она будет использовать
// UniqueUsernameValidator для предоставления логики валидации.
@Target(ElementType.FIELD)
//указывает, что UniqueUsername может быть применена только к полям. дДля расширения возможности использования
// этой аннотации (например, применять к методам), можно добавить дополнительные элементы типа в эту аннотацию.
@Retention(RetentionPolicy.RUNTIME)
//указывает, что аннотация будет доступна во время выполнения (а не только на этапе компиляции).
// Это необходимо, чтобы Spring мог обнаружить и обрабатывать аннотацию во время выполнения.
public @interface UniqueUsername {
    /**
     * Default error message to be used when validation fails.
     * This can be overridden by the user of the annotation.
     *
     * The string value here serves as a key to retrieve the actual message from a message resource.
     */
    String message() default "{custom.grimacegram.userName.UniqueUsername.message}";
    //      Groups can be used to control the order of validation. Not used in this context.
    Class<?>[] groups() default {};
    //      Payload provides custom details about the validation failure. Not used in this context.
    Class<? extends Payload>[] payload() default {};
}
