package com.example.demo.models;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String role;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Collection<Person> persons;


    // constructor
    private Authority() {
        persons = new HashSet<>();
    }


    @Override
    public String toString() {
        return role;
    }

}
