package com.student_loan.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private String password;
    
    @Column(name = "telephone_number")
    private String telephoneNumber;
    
    private String address;
    
    @Column(name = "university_degree")
    private String universityDegree;
    
    @Column(name = "degree_type")
    private String degreeType;
    
    @Column(name = "degree_year")
    private Integer degreeYear;
    
    private Integer penalties;
    
    @Column(name = "average_rating")
    private Double averageRating;
    
    private Boolean admin;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelephoneNumber() { return telephoneNumber; }
    public void setTelephoneNumber(String telephoneNumber) { this.telephoneNumber = telephoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getUniversityDegree() { return universityDegree; }
    public void setUniversityDegree(String universityDegree) { this.universityDegree = universityDegree; }

    public String getDegreeType() { return degreeType; }
    public void setDegreeType(String degreeType) { this.degreeType = degreeType; }

    public Integer getDegreeYear() { return degreeYear; }
    public void setDegreeYear(Integer degreeYear) { this.degreeYear = degreeYear; }

    public Integer getPenalties() { return penalties; }
    public void setPenalties(Integer penalties) { this.penalties = penalties; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Boolean getAdmin() { return admin; }
    public void setAdmin(Boolean admin) { this.admin = admin; }
}
