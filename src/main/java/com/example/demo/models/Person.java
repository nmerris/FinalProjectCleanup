package com.example.demo.models;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;

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

    private String selectVal;

//    @NotEmpty
    private String password;

    private String contactNum;

    // all usernames must be unique
//    @NotEmpty
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

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    private Collection<RegistrationTimestamp> timeStamps;

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    private Collection<Attendance> attendances;


    public Person() {
        this.authorities = new HashSet<>();
        this.courses = new HashSet<>();
        this.timeStamps = new HashSet<>();
        this.attendances = new HashSet<>();
    }

    // helper methods ==================================================================================
    public void addAuthority(Authority authority) {
        authorities.add(authority);
    }


    // normal getters/setters ==================================================================================
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNameFirst() {
        return nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public String getNameLast() {
        return nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSelectVal() {
        return selectVal;
    }

    public void setSelectVal(String selectVal) {
        this.selectVal = selectVal;
    }

    public Collection<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<Authority> authorities) {
        this.authorities = authorities;
    }

    public Collection<Course> getCourses() {
        return courses;
    }

    public void setCourses(Collection<Course> courses) {
        this.courses = courses;
    }

    public Collection<RegistrationTimestamp> getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(Collection<RegistrationTimestamp> timeStamps) {
        this.timeStamps = timeStamps;
    }

    public Collection<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(Collection<Attendance> attendances) {
        this.attendances = attendances;
    }

}
