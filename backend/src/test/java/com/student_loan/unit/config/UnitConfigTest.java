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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.student_loan.security.JwtUtil;
import com.student_loan.config.SecurityConfig;
import com.student_loan.config.CorsConfig;
import com.student_loan.config.TestSecurityConfig;

class UnitConfigTest {

    @Configuration
    static class TestJwtConfig {
        @Bean
        public JwtUtil jwtUtil() {
            return mock(JwtUtil.class);
        }

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
    }

    @Test
    void testSecurityFilterChainBeanExists() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(TestJwtConfig.class, SecurityConfig.class);
        ctx.refresh();

        SecurityFilterChain chain = ctx.getBean(SecurityFilterChain.class);
        assertNotNull(chain);
    }

    @Test
    void testCorsConfigurationSourceBeanExists() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(TestJwtConfig.class, SecurityConfig.class);
        ctx.refresh();

        CorsConfigurationSource source = ctx.getBean("corsConfigurationSource", CorsConfigurationSource.class);
        assertNotNull(source);
    }

    @Test
    void testCorsConfigurationSourceSettings() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(TestJwtConfig.class, SecurityConfig.class);
        ctx.refresh();

        CorsConfigurationSource source = ctx.getBean("corsConfigurationSource", CorsConfigurationSource.class);
        assertTrue(source instanceof UrlBasedCorsConfigurationSource);
        UrlBasedCorsConfigurationSource urlSource = (UrlBasedCorsConfigurationSource) source;

        var configs = urlSource.getCorsConfigurations();
        CorsConfiguration cfg = configs.get("/**");

        assertNotNull(cfg);
        assertEquals(List.of("http://localhost:3000"), cfg.getAllowedOrigins());
        assertEquals(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"), cfg.getAllowedMethods());
        assertEquals(List.of("Authorization", "Content-Type"), cfg.getAllowedHeaders());
        assertTrue(cfg.getAllowCredentials());
    }

    @Test
    void testCorsConfigurerBeanExists() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CorsConfig.class);
        WebMvcConfigurer configurer = context.getBean(WebMvcConfigurer.class);
        assertNotNull(configurer);
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

    @Test
    void testTestSecurityConfigBeans() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().setActiveProfiles("test");
        ctx.register(TestSecurityConfig.class);
        ctx.refresh();

        BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
        assertNotNull(encoder);

        SecurityFilterChain chain = ctx.getBean(SecurityFilterChain.class);
        assertNotNull(chain);
    }
}