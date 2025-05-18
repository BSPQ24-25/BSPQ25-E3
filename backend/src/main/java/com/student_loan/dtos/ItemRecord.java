package com.student_loan.dtos;


public record ItemRecord(
		
		String name,
		String description,
		String category,
		String purchasePrice, 
		String imageBase64,
		String status,
		String condition) {}