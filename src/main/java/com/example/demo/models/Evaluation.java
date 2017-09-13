package com.example.demo.models;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "evaluation_id"), inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Collection<Person> persons;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;


    // Rating owns Evaluation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_content_rating_id")
    private Rating courseContentRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_quality_rating_id")
    private Rating instructorQualityRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_experience_rating_id")
    private Rating trainingExperienceRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "textbook_rating_id")
    private Rating textbookRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_environment_id")
    private Rating classroomEnvironmentRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_equipment_id")
    private Rating classroomEquipmentRating;





}
