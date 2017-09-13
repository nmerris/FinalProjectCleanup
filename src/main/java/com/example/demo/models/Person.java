package com.example.demo.models;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;

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





}
