package com.example.demo.models;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

@Entity
public class CourseInfoRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // IF a log is attached to a current student (ie Person), this will should point to that student
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    // a log will always be associated with a particular course, although this does not need to be so
    // TODO allow logs to be entered that do not tie to a specific course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // this is what the person making the information request was asking about, like "Who is teaching Physics 101?"
    @NotEmpty
    private String description;

    // a log has it's own email and contact fields.. these should be filled in with whatever the caller/emailer used
    // note that this may be different than an existing students contact number/email
    // only an email OR a contact number are required, not both, this is enforced in AdminController
    @Email
    private String email;

    private String contactNum;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }
}
