// This class is to protect our applications routes,
// this is, we will only have public the routes to login and register
// and once we have a token granted we can access the rest.

package com.student_loan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/**").permitAll() // Public routes (login, register)
                .anyRequest().authenticated() // All other routes require authentication
            );
        return http.build();
    }
}
