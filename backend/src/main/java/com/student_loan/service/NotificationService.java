package com.student_loan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

	
    @Autowired
    private JavaMailSender mailSender;
    
    //Logger
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        try {
            mailSender.send(mensaje);
		} catch (Exception e) {
			logger.error("Error al enviar el correo: " + e.getMessage());
		}
    }
}