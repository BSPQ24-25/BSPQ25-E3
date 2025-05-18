package com.student_loan.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.student_loan.service.NotificationService;

class NotificationServiceTests {

    private NotificationService notificationService;
    private JavaMailSender mailSenderMock;

    @BeforeEach
    void setUp() {
        mailSenderMock = mock(JavaMailSender.class);
        notificationService = new NotificationService();
        // inject mock mailSender
        ReflectionTestUtils.setField(notificationService, "mailSender", mailSenderMock);
    }

    @Test
    @DisplayName("enviarCorreo - Should send email with correct fields")
    void testEnviarCorreo_Success() {
        // Arrange
        String to = "user@example.com";
        String subject = "Test Subject";
        String body = "Hello World";

        // Act
        assertDoesNotThrow(() -> notificationService.enviarCorreo(to, subject, body));

        // Assert: mailSender.send called once with message having correct fields
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSenderMock, times(1)).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertArrayEquals(new String[]{to}, sent.getTo());
        assertEquals(subject, sent.getSubject());
        assertEquals(body, sent.getText());
    }

    @Test
    @DisplayName("enviarCorreo - Should catch exception without throwing")
    void testEnviarCorreo_ExceptionHandled() {
        // Arrange: mailSender.send throws
        doThrow(new RuntimeException("SMTP failure")).when(mailSenderMock).send(any(SimpleMailMessage.class));
        String to = "fail@example.com";

        // Act & Assert: no exception escapes, send attempted
        assertDoesNotThrow(() -> notificationService.enviarCorreo(to, "subj", "body"));
        verify(mailSenderMock, times(1)).send(any(SimpleMailMessage.class));
    }
}
