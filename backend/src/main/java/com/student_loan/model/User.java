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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "degree_type")
    private DegreeType degreeType;
    
    @Column(name = "degree_year")
    private Integer degreeYear;
    
    private Integer penalties;
    
    @Column(name = "average_rating")
    private Double averageRating;
    
    private Boolean admin;

    // Enum para DegreeType
    public enum DegreeType {
        UNIVERSITY_DEGREE,
        MASTER,
        DOCTORATE
    }

    // Empty constructor
    public User() {
    	
    	
    }

    // Constructor
    public User(Long id, String name, String email, String password, String telephoneNumber, 
                String address, DegreeType degreeType, Integer degreeYear, Integer penalties, 
                Double averageRating, Boolean admin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.telephoneNumber = telephoneNumber;
        this.address = address;
        this.degreeType = degreeType;
        this.degreeYear = degreeYear;
        this.penalties = penalties;
        this.averageRating = averageRating;
        this.admin = admin;
    }

    // Getters & Setters
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

    public DegreeType getDegreeType() { return degreeType; }
    public void setDegreeType(DegreeType degreeType) { this.degreeType = degreeType; }

    public Integer getDegreeYear() { return degreeYear; }
    public void setDegreeYear(Integer degreeYear) { this.degreeYear = degreeYear; }

    public Integer getPenalties() { return penalties; }
    public void setPenalties(Integer penalties) { this.penalties = penalties; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Boolean getAdmin() { return admin; }
    public void setAdmin(Boolean admin) { this.admin = admin; }

    // toString
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", degreeType=" + degreeType +
                ", degreeYear=" + degreeYear +
                ", penalties=" + penalties +
                ", averageRating=" + averageRating +
                ", admin=" + admin +
                '}';
    }
}
