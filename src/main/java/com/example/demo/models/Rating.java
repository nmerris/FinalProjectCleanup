package com.example.demo.models;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // descriptions need to be seeded before deploymen
    // need 5 rows: Excellent, Above Average, Average, Fair, Poor
    private String description;

    @OneToMany(mappedBy = "courseContentRating", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<Evaluation> courseContentEvaluations;

    @OneToMany(mappedBy = "instructorQualityRating", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<Evaluation> instructorQualityEvaluations;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
