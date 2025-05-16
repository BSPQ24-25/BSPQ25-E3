package com.student_loan.unit.config;

import com.student_loan.config.CorsConfig;
import com.student_loan.config.SecurityConfig;
import com.student_loan.config.SecurityConfigTest;
import com.student_loan.security.JwtFilter;
import com.student_loan.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitConfigTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setupJwtUtil() {
        jwtUtil = new JwtUtil();
        SecurityContextHolder.clearContext();
    }

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
    void testGenerateTokenAndExtractEmail() {
        String email = "user@example.com";
        String token = jwtUtil.generateToken(email);

        assertNotNull(token, "El token no debe ser null");
        assertTrue(jwtUtil.validateToken(token), "El token generado debe ser válido");
        assertEquals(email, jwtUtil.extractEmail(token), "El email extraído debe coincidir con el original");
    }

    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.example";
        assertFalse(jwtUtil.validateToken(invalidToken), "Un token malformado debe considerarse inválido");
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
        ctx.register(SecurityConfigTest.class);
        ctx.refresh();

        BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
        assertNotNull(encoder);

        SecurityFilterChain chain = ctx.getBean(SecurityFilterChain.class);
        assertNotNull(chain);
    }

    // Nuevo test para JwtFilter.doFilterInternal
    @Test
    void testDoFilterInternal_withValidToken_setsAuthentication() throws ServletException, IOException {
        // Mock dependencies
        JwtUtil mockJwtUtil = mock(JwtUtil.class);
        JwtFilter filter = new JwtFilter(mockJwtUtil);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        String token = "valid.token";
        String email = "user@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtUtil.validateToken(token)).thenReturn(true);
        when(mockJwtUtil.extractEmail(token)).thenReturn(email);

        // Ejecutar filtro
        filter.doFilterInternal(request, response, chain);

        // Verificar que la autenticación se haya establecido
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // Verificar que continúe la cadena de filtros
        verify(chain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_withoutBearerHeader_skipsAuthentication() throws ServletException, IOException {
        JwtUtil mockJwtUtil = mock(JwtUtil.class);
        JwtFilter filter = new JwtFilter(mockJwtUtil);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_withInvalidToken_skipsAuthentication() throws ServletException, IOException {
        JwtUtil mockJwtUtil = mock(JwtUtil.class);
        JwtFilter filter = new JwtFilter(mockJwtUtil);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        String token = "invalid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtUtil.validateToken(token)).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}