package com.student_loan.unit.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.student_loan.security.JwtUtil;
import com.student_loan.config.SecurityConfig;
import com.student_loan.config.CorsConfig;

class UnitConfigTest {

    @Configuration
    static class TestJwtConfig {
        @Bean
        public JwtUtil jwtUtil() {
            return mock(JwtUtil.class);
        }

        // Provide MVC introspector so SecurityConfig's CORS can wire correctly
        @Bean
        public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
            return new HandlerMappingIntrospector();
        }
    }

    @Test
    void testBCryptPasswordEncoderBean() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(TestJwtConfig.class, SecurityConfig.class);
        ctx.refresh();

        BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
        assertNotNull(encoder);
        ctx.close();
    }

    @Test
    void testSecurityFilterChainBeanExists() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(TestJwtConfig.class, SecurityConfig.class);
        ctx.refresh();

        SecurityFilterChain chain = ctx.getBean(SecurityFilterChain.class);
        assertNotNull(chain);
        ctx.close();
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