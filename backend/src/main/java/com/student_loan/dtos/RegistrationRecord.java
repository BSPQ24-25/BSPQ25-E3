package com.student_loan.dtos;

public record RegistrationRecord(String name, String lastName, String email, String password, String telephoneNumber, String address, String degreeType, Integer degreeYear) {}