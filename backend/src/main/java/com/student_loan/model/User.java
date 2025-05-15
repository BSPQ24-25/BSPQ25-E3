
package com.student_loan.model;

import jakarta.persistence.*;

/**
 * Entity class representing a user in the system. Maps to the "users" table in the database.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * The telephone number of the user.
     */
    @Column(name = "telephone_number")
    private String telephoneNumber;

    /**
     * The address of the user.
     */
    private String address;

    /**
     * The degree type of the user (e.g., UNIVERSITY_DEGREE, MASTER, DOCTORATE).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "degree_type")
    private DegreeType degreeType;

    /**
     * The year of the degree the user is pursuing or has completed.
     */
    @Column(name = "degree_year")
    private Integer degreeYear;

    /**
     * The number of penalties the user has accumulated.
     */
    private Integer penalties;

    /**
     * The average rating of the user.
     */
    @Column(name = "average_rating")
    private Double averageRating;

    /**
     * Indicates whether the user has admin privileges.
     */
    private Boolean admin;

    /**
     * Enum representing the possible degree types of a user.
     */
    public enum DegreeType {
        UNIVERSITY_DEGREE,
        MASTER,
        DOCTORATE
    }

    /**
     * Default constructor for the User class.
     */
    public User() {
    }

    /**
     * Constructor for the User class.
     *
     * @param id             The unique identifier for the user.
     * @param name           The name of the user.
     * @param email          The email of the user.
     * @param password       The password of the user.
     * @param telephoneNumber The telephone number of the user.
     * @param address        The address of the user.
     * @param degreeType     The degree type of the user.
     * @param degreeYear     The year of the degree the user is pursuing or has completed.
     * @param penalties      The number of penalties the user has accumulated.
     * @param averageRating  The average rating of the user.
     * @param admin          Indicates whether the user has admin privileges.
     */
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

    /**
     * Gets the unique identifier for the user.
     *
     * @return The unique identifier for the user.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param id The unique identifier for the user.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name The name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email of the user.
     *
     * @return The email of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email The email of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The password of the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the telephone number of the user.
     *
     * @return The telephone number of the user.
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * Sets the telephone number of the user.
     *
     * @param telephoneNumber The telephone number of the user.
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * Gets the address of the user.
     *
     * @return The address of the user.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the user.
     *
     * @param address The address of the user.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the degree type of the user.
     *
     * @return The degree type of the user.
     */
    public DegreeType getDegreeType() {
        return degreeType;
    }

    /**
     * Sets the degree type of the user.
     *
     * @param degreeType The degree type of the user.
     */
    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }

    /**
     * Gets the year of the degree the user is pursuing or has completed.
     *
     * @return The year of the degree the user is pursuing or has completed.
     */
    public Integer getDegreeYear() {
        return degreeYear;
    }

    /**
     * Sets the year of the degree the user is pursuing or has completed.
     *
     * @param degreeYear The year of the degree the user is pursuing or has completed.
     */
    public void setDegreeYear(Integer degreeYear) {
        this.degreeYear = degreeYear;
    }

    /**
     * Gets the number of penalties the user has accumulated.
     *
     * @return The number of penalties the user has accumulated.
     */
    public Integer getPenalties() {
        return penalties;
    }

    /**
     * Sets the number of penalties the user has accumulated.
     *
     * @param penalties The number of penalties the user has accumulated.
     */
    public void setPenalties(Integer penalties) {
        this.penalties = penalties;
    }

    /**
     * Gets the average rating of the user.
     *
     * @return The average rating of the user.
     */
    public Double getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the average rating of the user.
     *
     * @param averageRating The average rating of the user.
     */
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Gets whether the user has admin privileges.
     *
     * @return True if the user has admin privileges, false otherwise.
     */
    public Boolean getAdmin() {
        return admin;
    }

    /**
     * Sets whether the user has admin privileges.
     *
     * @param admin True if the user has admin privileges, false otherwise.
     */
    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return A string representation of the user.
     */
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

