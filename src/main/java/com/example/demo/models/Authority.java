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

    @ManyToMany(mappedBy = "authorities", fetch = FetchType.EAGER)
    private Collection<Person> persons;


    // constructor
    private Authority() {
        persons = new HashSet<>();
    }


    @Override
    public String toString() {
        return role;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Collection<Person> getPersons() {
        return persons;
    }

    public void setPersons(Collection<Person> persons) {
        this.persons = persons;
    }
}
