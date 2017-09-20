package com.example.demo.models;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // @Temporal required for validation
    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "MMM d, yyyy")
    private Date dateStart;

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "MMM d, yyyy")
    private Date dateEnd;

    @NotEmpty
    private String name;

    @NotEmpty
    @Size(min=5,max =5 )
    private String courseRegistrationNum;

    // 0 = false, 1 = true
    private boolean deleted;


    // THIS DOES NOTHING, DO NOT USE THIS FIELD!!!
//    private long numEvaluationsCompleted;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Collection<RegistrationTimestamp> timeStamps;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Collection<Evaluation> evaluations;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Collection<Attendance> attendances;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Collection<CourseInfoRequestLog> courseInfoRequestLogs;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Collection<Person> persons;


    public Course() {
        this.timeStamps = new HashSet<>();
        this.evaluations = new HashSet<>();
        this.attendances = new HashSet<>();
        this.persons = new HashSet<>();
        this.courseInfoRequestLogs = new HashSet<>();
    }

    // helper methods ==================================================================================
    public void addPerson(Person person) {
        persons.add(person);
    }

    // use this to display the deleted status in the evaluations table
    public String getDeletedString() {
        return deleted ? "YES" : "NO";
    }

    // we are only allowing one teacher per course at this time, thus -1
    public long getNumStudents(){
        return persons.size()-1;
    }

    public long getNumInfoReq(){
        return courseInfoRequestLogs.size();
    }

    // BE CAREFUL WITH THIS ONE - IT RETURNS ALL THE EVALUATIONS FOR THIS COURSE, FOR ALL TEACHERS
    // which is what we want in admin course list, but NOT what we want in teacher course list
    public long getNumEvaluations(){return evaluations.size();}

    // use this is teacher course list to get the number of evals for a single course for a single teacher
    public long getNumEvalsByTeacherId(long teacherId) {
        long count = 0;
        for (Evaluation eval : evaluations) {
            if(eval.getPerson().getId() == teacherId) {
                count++;
            }
        }
        return count;
    }

    // use this to add a bunch of students to this course... only being used in admin section when updating an existing course
    public void addStudents(Set<Person> students) {
        persons.addAll(students);
    }

    // normal getter/setter methods ==================================================================================


    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getCourseRegistrationNum() {
        return courseRegistrationNum;
    }

    public void setCourseRegistrationNum(String courseRegistrationNum) {
        this.courseRegistrationNum = courseRegistrationNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public long getNumEvaluationsCompleted() {
//        return numEvaluationsCompleted;
//    }

//    public void setNumEvaluationsCompleted(long numEvaluationsCompleted) {
//        this.numEvaluationsCompleted = numEvaluationsCompleted;
//    }

    public Collection<RegistrationTimestamp> getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(Collection<RegistrationTimestamp> timeStamps) {
        this.timeStamps = timeStamps;
    }

    public Collection<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(Collection<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public Collection<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(Collection<Attendance> attendances) {
        this.attendances = attendances;
    }

    public Collection<Person> getPersons() {
        return persons;
    }

    public void setPersons(Collection<Person> persons) {
        this.persons = persons;
    }

    public Collection<CourseInfoRequestLog> getCourseInfoRequestLogs() {
        return courseInfoRequestLogs;
    }

    public void setCourseInfoRequestLogs(Collection<CourseInfoRequestLog> courseInfoRequestLogs) {
        this.courseInfoRequestLogs = courseInfoRequestLogs;
    }
}
