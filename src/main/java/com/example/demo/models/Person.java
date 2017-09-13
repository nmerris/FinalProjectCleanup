package com.example.demo.models;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty
    @Size(max = 50)
    private String nameFirst;

    @NotEmpty
    @Size(max = 50)
    private String nameLast;

    @Column(nullable = false)
    @NotEmpty
    @Email
    @Size(max = 50)
    private String email;

    @NotEmpty
    private String password;

    // all usernames must be unique
    @NotEmpty
    @Column(unique = true)
    private String username;

    private boolean enabled;



    // Authority is owner of Person
    // MUST use EAGER here, or you can't log in at all!!
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private Collection<Authority> authorities;

    // Person is owner of Course
    @ManyToMany(mappedBy = "persons", fetch = FetchType.LAZY)
    private Collection<Course> courses;

    // Person is owner of Evaluation
    @ManyToMany(mappedBy = "persons", fetch = FetchType.LAZY)
    private Collection<Evaluation> evaluations;






}
