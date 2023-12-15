package com.grimacegram.grimacegram.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * This class provides security configurations for the application, defining the authentication and authorization strategies.
 */

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    AuthUserService authUserService; // Service for user authentication.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().disable();
        http.httpBasic().authenticationEntryPoint(new BasicAuthenticationEntryPoint());

        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/1.0/login").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/1.0/users/{id:[0-9]+}").authenticated()
                .antMatchers(HttpMethod.POST, "/api/1.0/grimace/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/1.0/grimace/{id:[0-9]+}").authenticated()
                .and()
                .authorizeRequests().anyRequest().permitAll();
        http
                .sessionManagement()
                // Set the session management policy. Here, we're using STATELESS sessions.
                // This means the system won't create, use, or update any session data.
                // Typically, used in APIs where JWT (JSON Web Tokens) or other token-based authentication methods are used.
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    /**
     * Configures the authentication manager with custom user details service and password encoder.
     *
     * @param auth an AuthenticationManagerBuilder that is used to create an AuthenticationManager.
     *
     * The AuthenticationManager is responsible for validating if a given username/password combination is valid.
     * This method customizes the default behavior by setting a custom UserDetailsService (our AuthUserService)
     * and a password encoder.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authUserService).passwordEncoder(passwordEncoder());
    }
    /**
     * Bean definition for password encoder to be used for password hashing and comparison.
     *
     * @return a PasswordEncoder instance, specifically a BCryptPasswordEncoder in this case.
     *
     * BCryptPasswordEncoder is a widely-used password hashing algorithm that automatically handles salt creation.
     * Salting is a security measure to protect against rainbow table attacks.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
