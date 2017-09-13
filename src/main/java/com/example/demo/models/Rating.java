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

    @OneToMany(mappedBy = "trainingExperienceRating", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<Evaluation> trainingExperienceRatings;

    @OneToMany(mappedBy = "textbookRating", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<Evaluation> textBookRatings;

    @OneToMany(mappedBy = "classroomEnvironmentRating", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<Evaluation> classroomEnvironmentRatings;

    @OneToMany(mappedBy = "classroomEquipmentRating", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<Evaluation> classroomEquipmentRatings;



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
