package com.student_loan.dtos;


public record UserRecord(String name, String lastName, String email, String password, String telephoneNumber, String address, String degreeType, Integer degreeYear, Integer penalties, Double averageRating, Boolean admin){}