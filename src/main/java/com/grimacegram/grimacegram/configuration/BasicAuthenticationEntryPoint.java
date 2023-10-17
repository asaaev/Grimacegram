package com.grimacegram.grimacegram.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom authentication entry point for handling basic authentication failures.
 * This entry point is triggered when a user attempts to access a protected resource
 * without providing valid authentication credentials or if the credentials are incorrect.
 *
 * It responds by sending a 401 Unauthorized error to the client.
 */
public class BasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Handles the authentication failure and sends an HTTP 401 Unauthorized error response.
     *
     * @param request the request leading to the authentication failure.
     * @param response the response object.
     * @param authException the exception leading to the authentication failure.
     *
     * Instead of redirecting to a login page (which is the default behavior in form-login scenarios),
     * this method sends a straightforward 401 error to the client, indicating that authentication is required.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.sendError(HttpStatus.UNAUTHORIZED.value(),HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }
}
