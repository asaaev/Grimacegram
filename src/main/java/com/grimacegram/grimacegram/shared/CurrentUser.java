package com.grimacegram.grimacegram.shared;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to inject the currently authenticated user into controller methods.
 * This annotation wraps around @AuthenticationPrincipal to provide a more domain-specific reference.
 * When used on a controller method parameter of type User, Spring will inject the currently authenticated user.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal
public @interface CurrentUser {

}
