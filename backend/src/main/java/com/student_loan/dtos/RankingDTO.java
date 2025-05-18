package com.student_loan.dtos;

public class RankingDTO {
    private Long userId;
    private String name;
    private Double averageRating;
    private Integer penalties;

    public RankingDTO(Long userId, String name, Double averageRating, Integer penalties) {
        this.userId       = userId;
        this.name         = name;
        this.averageRating= averageRating;
        this.penalties    = penalties;
    }

    public Long   getUserId()       { return userId; }
    public String getName()         { return name; }
    public Double getAverageRating(){ return averageRating; }
    public Integer getPenalties()   { return penalties; }
}
