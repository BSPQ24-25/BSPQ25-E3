package com.student_loan.dtos;

// This DTO is to not expose all the information of the users when doing some API calls

public class UserDTO {
    private Long id;
    private String username;
    private String email;

    // constructor
    public UserDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }

    public String getEmail() { return email; }
}
