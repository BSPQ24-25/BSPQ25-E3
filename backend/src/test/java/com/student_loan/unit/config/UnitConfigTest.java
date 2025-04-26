package com.student_loan.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigUnitTests {

    @Test
    void testBCryptPasswordEncoderBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SecurityConfig.class);
        BCryptPasswordEncoder encoder = context.getBean(BCryptPasswordEncoder.class);
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
        context.close();
    }

    @Test
    void testSecurityFilterChainBeanExists() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SecurityConfig.class);
        SecurityFilterChain chain = context.getBean(SecurityFilterChain.class);
        assertNotNull(chain);
        context.close();
    }

    @Test
    void testCorsConfigurerBeanExists() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CorsConfig.class);
        WebMvcConfigurer configurer = context.getBean(WebMvcConfigurer.class);
        assertNotNull(configurer);
        context.close();
    }

    @Test
    void testAddCorsMappings() {
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class, RETURNS_SELF);
        when(registry.addMapping("/**")).thenReturn(registration);

        CorsConfig config = new CorsConfig();
        WebMvcConfigurer configurer = config.corsConfigurer();
        configurer.addCorsMappings(registry);

        verify(registry).addMapping("/**");
        verify(registration).allowedOrigins("http://localhost:3000");
        verify(registration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(registration).allowedHeaders("*");
        verify(registration).allowCredentials(true);
    }
}