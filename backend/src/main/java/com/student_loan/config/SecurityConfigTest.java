package com.student_loan.config;

import com.student_loan.security.JwtFilter;
import com.student_loan.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Profile("test")
public class SecurityConfigTest {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public JwtFilter jwtFilter(JwtUtil jwtUtil) {
        return new JwtFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   @Autowired JwtFilter jwtFilter) throws Exception {
        http.csrf().disable()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/users/**",
                    "/items/**",
                    "/loans/**"
                ).permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
