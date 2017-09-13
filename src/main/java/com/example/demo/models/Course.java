package com.example.demo.models;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // @Temporal required for validation
    // @DateTimeFormat will show the date as given, but ONLY when being pulled out of db, will still be stored
    // as full java.util.Date, which is what I want, for consistency
    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "MMM d, yyyy")
    private Date dateStart;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "MMM d, yyyy")
    private Date dateEnd;

    @NotEmpty
    private String title;




    // Person owns Course
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Collection<Person> persons;




}
