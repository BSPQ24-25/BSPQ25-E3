package com.student_loan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentLoanController {

    // Mapear la raíz a "/" para devolver "OK"
    @GetMapping("/")
    public String home() {
        return "OK"; // Respuesta simple
    }
    
    // Para confirmar que el servidor está activo, puedes probar otro endpoint
    @GetMapping("/status")
    public String status() {
        return "Service is running"; // Otro punto para probar la aplicación
    }
}